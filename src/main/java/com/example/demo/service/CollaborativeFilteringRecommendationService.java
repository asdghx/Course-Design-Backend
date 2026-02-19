package com.example.demo.service;

import com.example.demo.entity.Position;
import com.example.demo.entity.UserPositionHistory;
import com.example.demo.mapper.PositionMapper;
import com.example.demo.mapper.UserPositionHistoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 协同过滤推荐服务
 * 实现基于用户浏览记录的岗位推荐算法
 * 使用余弦相似度计算岗位间相似度，为用户提供个性化推荐
 */
@Service
public class CollaborativeFilteringRecommendationService {

    @Autowired
    private UserPositionHistoryMapper userPositionHistoryMapper;

    @Autowired
    private PositionMapper positionMapper;

    /**
     * 构建用户-浏览岗位映射关系
     * @return Map<userAccount, Set<positionId>> 用户账号到岗位ID集合的映射
     */
    public Map<String, Set<Integer>> getUserBrowseJobMap() {
        List<UserPositionHistory> allHistories = userPositionHistoryMapper.getAllUserPositionHistories();
        
        Map<String, Set<Integer>> userBrowseJobMap = new HashMap<>();
        for (UserPositionHistory history : allHistories) {
            String userAccount = history.getUserAccount();
            Integer positionId = history.getPositionId();
            
            userBrowseJobMap.computeIfAbsent(userAccount, k -> new HashSet<>()).add(positionId);
        }
        return userBrowseJobMap;
    }

    /**
     * 计算岗位间相似度矩阵
     * 使用余弦相似度算法计算岗位之间的相似度
     * @return Map<positionId, Map<positionId, similarity>> 岗位相似度矩阵
     */
    public Map<Integer, Map<Integer, Double>> calculateJobSimilarities() {
        // 获取所有用户-岗位浏览记录
        Map<String, Set<Integer>> userBrowseJobMap = getUserBrowseJobMap();
        
        // 获取所有用户和岗位列表
        Set<String> allUsers = userBrowseJobMap.keySet();
        Set<Integer> allJobs = new HashSet<>();
        for (Set<Integer> jobs : userBrowseJobMap.values()) {
            allJobs.addAll(jobs);
        }
        
        // 构建用户-岗位评分矩阵（浏览记为1分）
        Map<String, Map<Integer, Integer>> userJobMatrix = new HashMap<>();
        for (Map.Entry<String, Set<Integer>> entry : userBrowseJobMap.entrySet()) {
            String user = entry.getKey();
            for (Integer job : entry.getValue()) {
                userJobMatrix.computeIfAbsent(user, k -> new HashMap<>()).put(job, 1);
            }
        }
        
        // 计算岗位间的余弦相似度
        Map<Integer, Map<Integer, Double>> jobSimMatrix = new HashMap<>();
        List<Integer> jobIds = new ArrayList<>(allJobs);
        
        for (int i = 0; i < jobIds.size(); i++) {
            for (int j = i; j < jobIds.size(); j++) {
                Integer jobIdA = jobIds.get(i);
                Integer jobIdB = jobIds.get(j);
                
                // 计算向量点积、模长
                double dotProduct = 0.0;  // Σ(Ai * Bi)
                double normA = 0.0;       // √ΣAi²
                double normB = 0.0;       // √ΣBi²
                
                for (String user : allUsers) {
                    int scoreA = userJobMatrix.getOrDefault(user, new HashMap<>()).getOrDefault(jobIdA, 0);
                    int scoreB = userJobMatrix.getOrDefault(user, new HashMap<>()).getOrDefault(jobIdB, 0);
                    
                    dotProduct += scoreA * scoreB;
                    normA += scoreA * scoreA;
                    normB += scoreB * scoreB;
                }
                
                // 计算余弦相似度
                double cosineSimilarity = 0.0;
                if (normA > 0 && normB > 0) {
                    cosineSimilarity = dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
                }
                
                // 存储相似度矩阵
                jobSimMatrix.computeIfAbsent(jobIdA, k -> new HashMap<>()).put(jobIdB, cosineSimilarity);
                if (!jobIdA.equals(jobIdB)) {
                    jobSimMatrix.computeIfAbsent(jobIdB, k -> new HashMap<>()).put(jobIdA, cosineSimilarity);
                }
            }
        }
        
        return jobSimMatrix;
    }

    /**
     * 为指定用户生成岗位推荐列表
     * 基于用户的浏览历史和岗位相似度进行协同过滤推荐
     * @param userAccount 目标用户账号
     * @return 推荐的岗位列表
     */
    public List<Position> recommendJobsForUser(String userAccount) {
        // 如果userAccount为空或null，返回随机推荐
        if (userAccount == null || userAccount.trim().isEmpty()) {
            return getRandomRecommendations(4, new HashSet<>());
        }
        
        // 获取用户已浏览的岗位
        Map<String, Set<Integer>> userBrowseJobMap = getUserBrowseJobMap();
        Set<Integer> userBrowsedJobs = userBrowseJobMap.get(userAccount);
        
        // 如果用户没有浏览记录，返回随机推荐
        if (userBrowsedJobs == null || userBrowsedJobs.isEmpty()) {
            return getRandomRecommendations(4, new HashSet<>());
        }
        
        // 计算岗位相似度
        Map<Integer, Map<Integer, Double>> jobSimMatrix = calculateJobSimilarities();
        
        // 计算推荐分数
        Map<Integer, Double> candidateScores = new HashMap<>();
        
        for (Integer browsedJobId : userBrowsedJobs) {
            Map<Integer, Double> similarities = jobSimMatrix.get(browsedJobId);
            if (similarities != null) {
                for (Map.Entry<Integer, Double> entry : similarities.entrySet()) {
                    Integer candidateJobId = entry.getKey();
                    Double similarity = entry.getValue();
                    
                    // 过滤掉用户已经浏览过的岗位
                    if (!userBrowsedJobs.contains(candidateJobId)) {
                        candidateScores.merge(candidateJobId, similarity, Double::sum);
                    }
                }
            }
        }
        
        // 按相似度降序排序，取TopN
        return candidateScores.entrySet().stream()
                .sorted(Map.Entry.<Integer, Double>comparingByValue().reversed())
                .limit(4)  // 只取前4个CF推荐
                .map(entry -> positionMapper.selectById(entry.getKey()))
                .filter(Objects::nonNull)  // 过滤可能不存在的岗位
                .collect(Collectors.toList());
    }

    /**
     * 获取随机推荐岗位
     * @param count 需要的数量
     * @param excludeIds 排除的ID集合
     * @return 随机推荐的岗位列表
     */
    public List<Position> getRandomRecommendations(int count, Set<Integer> excludeIds) {
        List<Position> allPositions = positionMapper.selectRandomPositions();
        List<Position> availablePositions = allPositions.stream()
                .filter(pos -> pos.getId() != null && !excludeIds.contains(pos.getId()))
                .collect(Collectors.toList());
        
        // 随机打乱并取前count个
        Collections.shuffle(availablePositions);
        return availablePositions.stream()
                .limit(count)
                .collect(Collectors.toList());
    }

}