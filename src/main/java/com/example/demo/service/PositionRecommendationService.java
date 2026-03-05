package com.example.demo.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.entity.Position;
import com.example.demo.mapper.PositionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

/**
 * 岗位推荐服务类
 * 专注于基于协同过滤算法的岗位推荐及分页功能
 */
@Service
public class PositionRecommendationService {
    
    @Autowired
    private CollaborativeFilteringRecommendationService collaborativeFilteringRecommendationService;
    
    @Autowired
    private PositionMapper positionMapper;

    /**
     * 根据用户账号获取分页推荐岗位
     * 第1页返回CF推荐结果，后续页面返回其他数据
     */
    public IPage<Position> getRecommendationsWithPage(String userAccount, Page<Position> page, String universityName) {
        // 参数校验
        if (page == null) {
            Page<Position> emptyPage = new Page<>();
            emptyPage.setCurrent(1L);
            emptyPage.setSize(8L);
            emptyPage.setRecords(new ArrayList<>());
            emptyPage.setTotal(0L);
            emptyPage.setPages(0L);
            return emptyPage;
        }
        
        long currentPage = Optional.ofNullable(page.getCurrent()).orElse(1L);
        long pageSize = Optional.ofNullable(page.getSize()).orElse(8L);
        
        // 确保参数合法
        if (currentPage < 1) currentPage = 1;
        if (pageSize < 1) pageSize = 8;
        
        // 如果是校内岗位，直接使用分页查询（不使用 CF 推荐）
        if (universityName != null && !universityName.trim().isEmpty()) {
            Page<Position> allPositionsPage = new Page<>(currentPage, pageSize);
            return positionMapper.selectPositionPage(allPositionsPage, null, universityName.trim());
        }
        
        if (currentPage == 1) {
            // 第 1 页：返回 CF 推荐结果
            List<Position> cfRecommendations = getCFRecommendations(userAccount, universityName);
            
            // 如果 CF 推荐不足一页，补充随机数据
            if (cfRecommendations.size() < pageSize) {
                List<Position> randomPositions = collaborativeFilteringRecommendationService.getRandomRecommendations(
                    (int) (pageSize - cfRecommendations.size()),
                    new HashSet<>(cfRecommendations.stream()
                        .filter(p -> p != null && p.getId() != null)
                        .map(Position::getId)
                        .collect(java.util.stream.Collectors.toSet()))
                );
                if (randomPositions != null) {
                    cfRecommendations.addAll(randomPositions);
                }
            }
            
            // 取前 pageSize 条数据
            int endIndex = Math.min((int) pageSize, cfRecommendations.size());
            List<Position> pagedResults = endIndex > 0 ? 
                new ArrayList<>(cfRecommendations.subList(0, endIndex)) : new ArrayList<>();
            
            // 构造返回结果
            Page<Position> resultPage = new Page<>();
            resultPage.setCurrent(page.getCurrent());
            resultPage.setSize(page.getSize());
            resultPage.setRecords(pagedResults);
            resultPage.setTotal((long) Math.max(cfRecommendations.size(), (int) pageSize));
            resultPage.setPages(10L);
            
            return resultPage;
        }
        
        // 后续页面：返回数据库中的岗位数据（根据 universityName 过滤）
        Page<Position> allPositionsPage = new Page<>(currentPage, pageSize);
        IPage<Position> allPositions = positionMapper.selectPositionPage(allPositionsPage, null, universityName);
        
        // 如果没有更多数据，返回空结果
        if (allPositions.getRecords() == null || allPositions.getRecords().isEmpty()) {
            Page<Position> emptyPage = new Page<>();
            emptyPage.setCurrent(page.getCurrent());
            emptyPage.setSize(page.getSize());
            emptyPage.setRecords(new ArrayList<>());
            emptyPage.setTotal(allPositions.getTotal());
            emptyPage.setPages(allPositions.getPages());
            return emptyPage;
        }
        
        return allPositions;
    }
    
    /**
     * 获取 CF 推荐结果（支持按学校名称过滤）
     */
    private List<Position> getCFRecommendations(String userAccount, String universityName) {
        List<Position> recommendations = new ArrayList<>();
            
            recommendations = collaborativeFilteringRecommendationService.recommendJobsForUser(userAccount == null ? "" : userAccount);
                
            // 过滤空值
            if (recommendations != null) {
                recommendations.removeIf(pos -> pos == null);
            } else {
                recommendations = new ArrayList<>();
            }
                
            // 根据 universityName 过滤：为 null 或空表示校外岗位，有值表示对应学校的校内岗位
            if (universityName == null || universityName.trim().isEmpty()) {
                // 校外观岗：过滤掉有 universityName 的岗位
                recommendations.removeIf(pos -> pos.getUniversityName() != null && !pos.getUniversityName().trim().isEmpty());
            } else {
                // 校内岗位：只保留匹配学校名称的岗位
                final String targetUniversity = universityName.trim();
                recommendations.removeIf(pos -> pos.getUniversityName() == null || !pos.getUniversityName().trim().equals(targetUniversity));
            }
                
            // 如果没有 CF 推荐或推荐不足，获取随机推荐作为补充
            if (recommendations.isEmpty()) {
                // 直接根据 universityName 获取对应的随机岗位
                List<Position> randomPositions;
                if (universityName == null || universityName.trim().isEmpty()) {
                    // 校外观岗：使用原有的随机推荐方法
                    randomPositions = collaborativeFilteringRecommendationService.getRandomRecommendations(8, new HashSet<>());
                } else {
                    // 校内岗位：需要获取对应学校的随机岗位，这里简化处理，先获取所有再过滤
                    randomPositions = collaborativeFilteringRecommendationService.getRandomRecommendations(20, new HashSet<>());
                    // 过滤出匹配学校的岗位
                    final String targetUni = universityName.trim();
                    if (randomPositions != null) {
                        randomPositions.removeIf(pos -> pos.getUniversityName() == null || !pos.getUniversityName().trim().equals(targetUni));
                        // 如果数量不足，继续补充
                        while (randomPositions.size() < 8) {
                            List<Position> more = collaborativeFilteringRecommendationService.getRandomRecommendations(10, new HashSet<>());
                            if (more == null || more.isEmpty()) break;
                            more.removeIf(pos -> pos.getUniversityName() == null || !pos.getUniversityName().trim().equals(targetUni));
                            randomPositions.addAll(more);
                        }
                    }
                }
                
                // 再次过滤空值
                if (randomPositions != null) {
                    randomPositions.removeIf(pos -> pos == null);
                    recommendations.addAll(randomPositions);
                }
            }
            
            return recommendations;
    }
}