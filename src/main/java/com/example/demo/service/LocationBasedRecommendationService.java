package com.example.demo.service;

import com.example.demo.entity.Position;
import com.example.demo.mapper.PositionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 基于地理位置的推荐服务
 * 实现基于用户位置的岗位推荐算法
 * 
 * 【职责定位】：LBS 推荐实现层，专注于地理位置相关的推荐逻辑
 */
@Service
public class LocationBasedRecommendationService {

    @Autowired
    private PositionMapper positionMapper;

    /**
     * 根据位置获取附近岗位推荐 (按距离排序)
     * 
     * @param latitude 用户纬度
     * @param longitude 用户经度
     * @param maxDistance 最大距离 (米)
     * @return 岗位列表 (按距离从近到远排序)
     */
    public List<Position> recommendByLocation(Double latitude, Double longitude, Double maxDistance) {
        // 参数校验
        if (latitude == null || longitude == null || maxDistance == null) {
            return new ArrayList<>();
        }
        
        // 校验距离范围 (0-1000km)
        if (maxDistance < 0 || maxDistance > 1000000) {
            return new ArrayList<>();
        }
        
        // 校验纬度范围 [-90, 90]
        if (latitude < -90 || latitude > 90) {
            return new ArrayList<>();
        }
        
        // 校验经度范围 [-180, 180]
        if (longitude < -180 || longitude > 180) {
            return new ArrayList<>();
        }
        
        // Step 1: 计算经纬度范围 (粗略过滤)
        // 纬度每度≈111km，经度每度≈111km×cos(纬度)
        Double latDelta = maxDistance / 111000.0;  // 纬度差
        Double lonDelta = maxDistance / (111000.0 * Math.cos(Math.toRadians(latitude))); // 经度差
        
        Double minLat = latitude - latDelta;
        Double maxLat = latitude + latDelta;
        Double minLon = longitude - lonDelta;
        Double maxLon = longitude + lonDelta;
        
        // Step 2: 查询候选岗位 (矩形区域过滤)
        List<Position> candidates = positionMapper.selectByLocationRange(minLat, maxLat, minLon, maxLon);
        
        // Step 3: 用 Haversine 公式精确计算距离并过滤
        List<Position> result = candidates.stream()
            .filter(p -> p.getLatitude() != null && p.getLongitude() != null) // 过滤没有经纬度的数据
            .map(p -> {
                double distance = calculateHaversine(
                    latitude, longitude, 
                    p.getLatitude(), p.getLongitude()
                );
                p.setDistance(distance); // 设置距离字段
                return p;
            })
            .filter(p -> p.getDistance() <= maxDistance) // 精确过滤超出范围的
            .sorted(Comparator.comparingDouble(Position::getDistance)) // 按距离排序
            .collect(Collectors.toList());
        
        return result;
    }

    /**
     * Haversine 公式计算两点间距离 (单位：米)
     * 
     * @param lat1 点 1 纬度
     * @param lon1 点 1 经度
     * @param lat2 点 2 纬度
     * @param lon2 点 2 经度
     * @return 距离 (米)
     */
    private double calculateHaversine(double lat1, double lon1, double lat2, double lon2) {
        final double R = 6371000; // 地球半径 (米)
        
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                 + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                 * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        
        // 限制 a 的范围在 [0, 1]，避免浮点数精度问题导致 sqrt(负数)
        double clampedA = Math.max(0.0, Math.min(1.0, a));
        double c = 2 * Math.atan2(Math.sqrt(clampedA), Math.sqrt(1.0 - clampedA));
        
        return R * c; // 距离 (米)
    }
}
