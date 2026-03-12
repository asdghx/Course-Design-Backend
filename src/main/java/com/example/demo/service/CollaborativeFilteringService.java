package com.example.demo.service;

import com.example.demo.common.CosineSimilarityUtil;
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
 * 
 * 【职责定位】：纯粹的 CF 算法实现层，不包含业务规则和降级逻辑
 */
@Service
public class CollaborativeFilteringService {

    @Autowired
    private UserPositionHistoryMapper userPositionHistoryMapper;

    @Autowired
    private PositionMapper positionMapper;

    /**
     * 为指定用户生成岗位推荐列表（纯 CF 算法，仅校外岗位）
     * 基于用户的浏览历史和岗位相似度进行协同过滤推荐
     * 
     * @param userAccount 目标用户账号
     * @param limit 返回数量限制（通常等于分页的 pageSize）
     * @return 推荐的岗位列表（可能为空）
     */
    public List<Position> recommendByCF(String userAccount, int limit) {
        if (userAccount == null || userAccount.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        // 查询所有浏览记录
        List<UserPositionHistory> allHistories = userPositionHistoryMapper.getAllUserPositionHistories();
        
        // 构建用户 - 岗位映射
        Map<String, Set<Integer>> userBrowseJobMap = new HashMap<>();
        for (UserPositionHistory history : allHistories) {
            userBrowseJobMap.computeIfAbsent(history.getUserAccount(), k -> new HashSet<>())
                    .add(history.getPositionId());
        }
        
        Set<Integer> userBrowsedJobs = userBrowseJobMap.get(userAccount);
        if (userBrowsedJobs == null || userBrowsedJobs.isEmpty()) {
            return new ArrayList<>();
        }
        
        // 计算岗位相似度矩阵
        Map<Integer, Map<Integer, Double>> jobSimMatrix = calculateJobSimilarities(allHistories, userBrowseJobMap);
        
        // 计算推荐分数
        Map<Integer, Double> candidateScores = new HashMap<>();
        for (Integer browsedJobId : userBrowsedJobs) {
            Map<Integer, Double> similarities = jobSimMatrix.get(browsedJobId);
            if (similarities != null) {
                for (Map.Entry<Integer, Double> entry : similarities.entrySet()) {
                    Integer candidateJobId = entry.getKey();
                    Double similarity = entry.getValue();
                    if (!userBrowsedJobs.contains(candidateJobId)) {
                        candidateScores.merge(candidateJobId, similarity, Double::sum);
                    }
                }
            }
        }
        
        // 按相似度降序排序，取 TopN（仅校外岗位）
        return candidateScores.entrySet().stream()
                .sorted(Map.Entry.<Integer, Double>comparingByValue().reversed())
                .limit(limit)
                .map(entry -> positionMapper.selectById(entry.getKey()))
                .filter(Objects::nonNull)
                .filter(pos -> pos.getUniversityName() == null || pos.getUniversityName().trim().isEmpty())
                .collect(Collectors.toList());
    }





    /**
     * 计算岗位间相似度矩阵
     * @param allHistories 所有浏览历史记录
     * @param userBrowseJobMap 用户 - 岗位映射
     * @return 岗位相似度矩阵
     */
    private Map<Integer, Map<Integer, Double>> calculateJobSimilarities(
            List<UserPositionHistory> allHistories,
            Map<String, Set<Integer>> userBrowseJobMap) {
        
        // 预计算岗位分数（浏览*1 + 投递*3 + 通过*5）
        Map<Integer, Integer> jobScoreMap = new HashMap<>();
        for (UserPositionHistory h : allHistories) {
            int posId = h.getPositionId();
            if (!jobScoreMap.containsKey(posId)) {
                int score = (h.getBrowseCount() != null ? h.getBrowseCount() : 0)
                          + (h.getDeliveryCount() != null ? h.getDeliveryCount() : 0) * 3
                          + (h.getPassCount() != null ? h.getPassCount() : 0) * 5;
                jobScoreMap.put(posId, score);
            }
        }
        
        // 构建岗位共现矩阵（基于用户共同浏览行为）
        Map<Integer, Map<Integer, Integer>> coBrowseMatrix = new HashMap<>();
        for (UserPositionHistory h : allHistories) {
            int posId = h.getPositionId();
            String user = h.getUserAccount();
            Integer weight = jobScoreMap.get(posId);
            
            // 将该岗位与该用户浏览的其他岗位建立关联
            Set<Integer> userBrowsedJobs = userBrowseJobMap.get(user);
            if (userBrowsedJobs == null) continue;  // 跳过空值
            
            for (Integer otherPosId : userBrowsedJobs) {
                if (posId != otherPosId) {  // 直接用 != 比较
                    // 岗位 posId 和 otherPosId 被同一用户浏览，增加共现强度
                    coBrowseMatrix.computeIfAbsent(posId, k -> new HashMap<>())
                                  .merge(otherPosId, weight, Integer::sum);
                }
            }
        }
        
        return CosineSimilarityUtil.calculateCosineSimilarity(coBrowseMatrix);
    }



}
