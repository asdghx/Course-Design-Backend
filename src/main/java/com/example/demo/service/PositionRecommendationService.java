package com.example.demo.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.entity.Employer;
import com.example.demo.entity.Position;
import com.example.demo.entity.vo.PositionVO;
import com.example.demo.mapper.EmployerMapper;
import com.example.demo.mapper.PositionMapper;
import com.example.demo.common.PageResultBuilder;
import com.example.demo.common.PositionVOConverter;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 岗位推荐服务类
 * 专注于基于协同过滤算法的岗位推荐及分页功能
 */
@Service
public class PositionRecommendationService {
    
    private final CollaborativeFilteringService collaborativeFilteringService;
    private final LocationBasedRecommendationService locationBasedRecommendationService;
    private final PositionMapper positionMapper;
    private final EmployerMapper employerMapper;
    public PositionRecommendationService(CollaborativeFilteringService collaborativeFilteringService,
                                         LocationBasedRecommendationService locationBasedRecommendationService,
                                         PositionMapper positionMapper,
                                         EmployerMapper employerMapper) {
        this.collaborativeFilteringService = collaborativeFilteringService;
        this.locationBasedRecommendationService = locationBasedRecommendationService;
        this.positionMapper = positionMapper;
        this.employerMapper = employerMapper;
    }

    /**
     * 根据位置获取附近岗位推荐（LBS 推荐）
     * 
     * @param latitude 用户纬度
     * @param longitude 用户经度
     * @param maxDistance 最大距离 (米)
     * @return 岗位列表 (按距离从近到远排序)
     */
    public List<PositionVO> getLocationRecommendations(Double latitude, Double longitude, Double maxDistance) {
        // 调用 LBS 推荐实现层
        List<Position> positions = locationBasedRecommendationService.recommendByLocation(latitude, longitude, maxDistance);
        
        // 转换为 VO 并返回
        return positions.stream()
                .map(p -> PositionVOConverter.convertToVO(p, employerMapper))
                .collect(Collectors.toList());
    }
    
    /**
     * 根据用户账号获取分页推荐岗位
     * 第 1 页返回 CF 推荐结果，后续页面返回其他数据
     */
    public Page<PositionVO> getPositionRecommendations(String userAccount, Page<PositionVO> page, String universityName) {
        long currentPage = Optional.ofNullable(page.getCurrent()).orElse(1L);
        long pageSize = Optional.ofNullable(page.getSize()).orElse(8L);
        
        // 确保参数合法
        if (currentPage < 1) currentPage = 1;
        if (pageSize < 1) pageSize = 8;
        
        // 如果是校内岗位，直接使用分页查询（不使用 CF 推荐）
        if (universityName != null && !universityName.trim().isEmpty()) {
            return getCampusPositionsPage(currentPage, pageSize, universityName.trim());
        }
        
        if (currentPage == 1) {
            // 第 1 页：返回 CF 推荐结果
            return getCFRecommendationsPage(userAccount, universityName, currentPage, pageSize);
        }
        
        // 后续页面：返回数据库中的岗位数据
        return getAllPositionsPage(currentPage, pageSize, universityName);
    }
    
    /**
     * 获取 CF 推荐结果（数据库已按学校过滤，无需额外处理）
     */
    private List<Position> getCFRecommendations(String userAccount, String universityName) {
        // Step 1: 获取 CF 推荐（纯算法，可能返回空）
        List<Position> recommendations = collaborativeFilteringService.recommendByCF(
            userAccount == null ? "" : userAccount
        );
        
        // 处理空值
        if (recommendations == null) {
            recommendations = new ArrayList<>();
        } else {
            recommendations.removeIf(Objects::isNull);
        }
        
        // Step 2: 如果 CF 推荐为空，使用随机推荐补充（降级策略在这里）
        if (recommendations.isEmpty()) {
            fillWithRandomRecommendations(recommendations, universityName);
        }
        
        return recommendations;
    }
    

    
    /**
     * 用随机推荐填充推荐列表（数据库已按学校过滤）
     * 
     * 【职责定位】：推荐策略层的降级逻辑实现
     */
    private void fillWithRandomRecommendations(List<Position> recommendations, String universityName) {
        // 直接获取随机推荐，数据库会自动按学校过滤
        List<Position> randomPositions = getRandomRecommendations(8, new HashSet<>());
        
        if (randomPositions != null) {
            randomPositions.removeIf(Objects::isNull);
            recommendations.addAll(randomPositions);
        }
    }
    
    /**
     * 获取随机推荐岗位（从推荐策略层提供，不在算法层）
     * @param count 需要的数量
     * @param excludeIds 排除的 ID 集合
     * @return 随机推荐的岗位列表
     */
    private List<Position> getRandomRecommendations(int count, Set<Integer> excludeIds) {
        List<Position> allPositions = positionMapper.selectRandomPositions();
        List<Position> availablePositions = allPositions.stream()
                .filter(pos -> pos.getId() != null && !excludeIds.contains(pos.getId()))
                .collect(Collectors.toList());
        
        // 随机打乱并取前 count 个
        Collections.shuffle(availablePositions);
        return availablePositions.stream()
                .limit(count)
                .collect(Collectors.toList());
    }
    

    
    /**
     * 获取校内岗位分页
     */
    private com.baomidou.mybatisplus.extension.plugins.pagination.Page<PositionVO> getCampusPositionsPage(long currentPage, long pageSize, String targetUniversity) {
        Page<Position> positionPage = new Page<>(currentPage, pageSize);
        IPage<Position> allPositions = positionMapper.selectPositionPage(positionPage, null, targetUniversity);
        
        List<PositionVO> voList = PositionVOConverter.convertFromPageRecords(allPositions, employerMapper);
        
        return PageResultBuilder.build(positionPage, voList);
    }
    
    /**
     * 获取 CF 推荐分页（第 1 页）
     */
    private Page<PositionVO> getCFRecommendationsPage(String userAccount, String universityName, long currentPage, long pageSize) {
        // 获取 CF 推荐
        List<Position> cfRecommendations = getCFRecommendations(userAccount, universityName);
        
        // 如果 CF 推荐不足一页，补充随机数据
        if (cfRecommendations.size() < pageSize) {
            Set<Integer> excludeIds = cfRecommendations.stream()
                .filter(p -> p != null && p.getId() != null)
                .map(Position::getId)
                .collect(Collectors.toSet());
            
            List<Position> randomPositions = getRandomRecommendations(
                (int) (pageSize - cfRecommendations.size()), excludeIds);
            
            if (randomPositions != null) {
                cfRecommendations.addAll(randomPositions);
            }
        }
        
        // 取前 pageSize 条数据并转换为 VO
        int endIndex = Math.min((int) pageSize, cfRecommendations.size());
        List<PositionVO> pagedResults = endIndex > 0 ? 
            PositionVOConverter.convertToVOList(cfRecommendations.subList(0, endIndex), employerMapper) : new ArrayList<>();
        
        // 构造返回结果
        Page<PositionVO> resultPage = PageResultBuilder.build(new Page<>(currentPage, pageSize), pagedResults);
        resultPage.setTotal(Math.max(cfRecommendations.size(), (int) pageSize));
        
        return resultPage;
    }
    
    /**
     * 获取所有岗位分页（后续页面）
     */
    private Page<PositionVO> getAllPositionsPage(long currentPage, long pageSize, String universityName) {
        Page<Position> positionPage = new Page<>(currentPage, pageSize);
        IPage<Position> allPositions = positionMapper.selectPositionPage(positionPage, null, universityName);
        
        // 如果没有更多数据，返回空结果
        if (allPositions.getRecords() == null || allPositions.getRecords().isEmpty()) {
            return createEmptyPage(currentPage, pageSize);
        }
        
        List<PositionVO> voList = PositionVOConverter.convertFromPageRecords(allPositions, employerMapper);
        
        return PageResultBuilder.build(positionPage, voList);
    }
    
    /**
     * 创建空分页结果
     */
    private Page<PositionVO> createEmptyPage(long current, long size) {
        return PageResultBuilder.buildEmpty(current, size);
    }
    
}