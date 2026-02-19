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
import java.util.concurrent.ConcurrentHashMap;
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
    
    // 缓存用户的CF推荐结果，仅用于第一页
    private final ConcurrentHashMap<String, List<Position>> userCFRecommendationCache = new ConcurrentHashMap<>();

    /**
     * 根据用户账号获取分页推荐岗位
     * 第1页返回CF推荐结果，后续页面返回其他数据
     */
    public IPage<Position> getRecommendationsWithPage(String userAccount, Page<Position> page) {
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
        
        if (currentPage == 1) {
            // 第1页：返回CF推荐结果
            List<Position> cfRecommendations = getCFRecommendations(userAccount);
            
            // 如果CF推荐不足一页，补充随机数据
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
            
            // 取前pageSize条数据
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
        
        // 后续页面：返回数据库中的岗位数据
        Page<Position> allPositionsPage = new Page<>(currentPage, pageSize);
        IPage<Position> allPositions = positionMapper.selectPage(allPositionsPage, null);
        
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
     * 获取CF推荐结果，使用缓存确保一致性
     */
    private List<Position> getCFRecommendations(String userAccount) {
        String key = userAccount == null ? "" : userAccount;
        
        return userCFRecommendationCache.computeIfAbsent(key, k -> {
            List<Position> recommendations = new ArrayList<>();
            
            // 获取CF推荐结果
            recommendations = collaborativeFilteringRecommendationService.recommendJobsForUser(k);
            
            // 过滤空值
            if (recommendations != null) {
                recommendations.removeIf(pos -> pos == null);
            } else {
                recommendations = new ArrayList<>();
            }
            
            // 如果没有CF推荐或推荐不足，获取随机推荐作为补充
            if (recommendations.isEmpty()) {
                recommendations = collaborativeFilteringRecommendationService.getRandomRecommendations(8, new HashSet<>());
                // 再次过滤空值
                if (recommendations != null) {
                    recommendations.removeIf(pos -> pos == null);
                } else {
                    recommendations = new ArrayList<>();
                }
            }
            
            return recommendations;
        });
    }
}