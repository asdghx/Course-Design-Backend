package com.example.demo.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.entity.Position;
import com.example.demo.mapper.PositionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 岗位管理服务类
 * 专注于岗位的CRUD操作
 */
@Service
public class PositionManagementService {
    @Autowired
    private PositionMapper positionMapper;

    /**
     * 根据企业账号分页查询岗位列表（HR 管理专用，不限制学校和状态）
     * @param employerAccount 企业账号（可选，为 null 时查询所有企业的岗位）
     * @param currentPage 当前页码
     * @param pageSize 每页大小
     * @return 岗位分页结果
     */
    public IPage<Position> getPositionListByEmployerAccountWithPagination(String employerAccount, Integer currentPage, Integer pageSize) {
        Page<Position> page = new Page<>(currentPage, pageSize);
        return positionMapper.selectHrPositions(page, employerAccount);
    }

    /**
     * 获取所有岗位的分页列表
     */
    public IPage<Position> getAllPositionsPaged(Integer currentPage, Integer pageSize) {
        Page<Position> page = new Page<>(currentPage, pageSize);
        return positionMapper.selectPage(page, null); // 使用BaseMapper的通用分页方法
    }

    /**
     * 根据 ID 删除岗位（软删除）
     */
    public String deletePositionById(Integer positionId){
        // TODO: 检查是否有投递记录关联
        // Long deliveryCount = resumeDeliveryMapper.selectCount(
        //     new QueryWrapper<ResumeDelivery>().eq("position_id", positionId)
        // );
        // if (deliveryCount > 0) {
        //     return "该岗位有" + deliveryCount + "条投递记录，不能删除，请设置为无效状态";
        // }
        
        // 软删除：设置 status=0
        Position position = new Position();
        position.setId(positionId);
        position.setStatus(0);  // 设置为无效
        int rows = positionMapper.updatePosition(position);
        
        return rows > 0 ? "删除岗位成功" : "删除岗位失败";
    }

    /**
     * 创建新岗位
     */
    public String createPosition(String employerAccount, Integer salaryMin, Integer salaryMax, String jobDescription, String workLocation, String universityName, Double latitude, Double longitude) {
        // 参数验证
        if (employerAccount == null || employerAccount.trim().isEmpty()) {
            return "雇主账号不能为空";
        }
        if (jobDescription == null || jobDescription.trim().isEmpty()) {
            return "岗位描述不能为空";
        }
        
        // 薪资校验
        if (salaryMin != null && salaryMax != null) {
            if (salaryMin < 0) {
                return "最低工资不能为负数";
            }
            if (salaryMax < 0) {
                return "最高工资不能为负数";
            }
            if (salaryMin > salaryMax) {
                return "最低工资不能高于最高工资";
            }
            if (salaryMin > 1000000 || salaryMax > 1000000) {
                return "工资金额过高，请输入合理值";
            }
        }
        
        // 经纬度校验
        if (latitude != null && (latitude < -90 || latitude > 90)) {
            return "纬度必须在 -90 到 90 之间";
        }
        if (longitude != null && (longitude < -180 || longitude > 180)) {
            return "经度必须在 -180 到 180 之间";
        }
        
        Position position = createPositionObject(employerAccount, salaryMin, salaryMax, jobDescription, workLocation, universityName, latitude, longitude);
        position.setStatus(1); // 默认有效状态
        int rows = positionMapper.insertPosition(position);
        return rows > 0 ? "添加岗位成功" : "添加岗位失败";
    }

    /**
     * 更新岗位信息
     */
    public String updatePositionById(Integer id, String employerAccount, Integer salaryMin, Integer salaryMax, String jobDescription, String workLocation, String universityName, Double latitude, Double longitude) {
        // 参数验证
        if (jobDescription == null || jobDescription.trim().isEmpty()) {
            return "岗位描述不能为空";
        }
        
        // 薪资校验
        if (salaryMin != null && salaryMax != null) {
            if (salaryMin < 0) {
                return "最低工资不能为负数";
            }
            if (salaryMax < 0) {
                return "最高工资不能为负数";
            }
            if (salaryMin > salaryMax) {
                return "最低工资不能高于最高工资";
            }
            if (salaryMin > 1000000 || salaryMax > 1000000) {
                return "工资金额过高，请输入合理值";
            }
        }
        
        // 经纬度校验
        if (latitude != null && (latitude < -90 || latitude > 90)) {
            return "纬度必须在 -90 到 90 之间";
        }
        if (longitude != null && (longitude < -180 || longitude > 180)) {
            return "经度必须在 -180 到 180 之间";
        }
        
        Position position = createPositionObject(employerAccount, salaryMin, salaryMax, jobDescription, workLocation, universityName, latitude, longitude);
        position.setId(id);  // 设置 ID 用于更新条件
        position.setStatus(1); // 保持有效状态
        int rows = positionMapper.updatePosition(position);
        return rows > 0 ? "更新岗位成功" : "更新岗位失败";
    }
    
    /**
     * 创建岗位对象的私有辅助方法
     */
    private Position createPositionObject(String employerAccount, Integer salaryMin, Integer salaryMax, String jobDescription, String workLocation, String universityName, Double latitude, Double longitude) {
        Position position = new Position();
        position.setEmployerAccount(employerAccount);
        position.setSalaryMin(salaryMin);
        position.setSalaryMax(salaryMax);
        position.setJobDescription(jobDescription);
        position.setWorkLocation(workLocation);
        position.setUniversityName(universityName); // 大学名称，为空表示校外岗位
        position.setLatitude(latitude);
        position.setLongitude(longitude);
        return position;
    }
    
    /**
     * 根据位置获取附近岗位 (按距离排序)
     * @param latitude 用户纬度
     * @param longitude 用户经度
     * @param maxDistance 最大距离 (米)
     * @return 岗位列表 (按距离从近到远排序)
     */
    public List<Position> getPositionsByLocation(Double latitude, Double longitude, Double maxDistance) {
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