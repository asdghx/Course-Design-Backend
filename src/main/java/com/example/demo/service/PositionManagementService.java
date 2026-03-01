package com.example.demo.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.entity.Position;
import com.example.demo.mapper.PositionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 岗位管理服务类
 * 专注于岗位的CRUD操作
 */
@Service
public class PositionManagementService {
    @Autowired
    private PositionMapper positionMapper;

    /**
     * 根据企业账号分页查询岗位列表
     */
    @Cacheable(value = "positions", key = "#employerAccount + '_' + #currentPage + '_' + #pageSize")
    public IPage<Position> getPositionListByEmployerAccountWithPagination(String employerAccount, Integer currentPage, Integer pageSize){
        Page<Position> page = new Page<>(currentPage, pageSize);
        return positionMapper.selectPositionPage(page, employerAccount);
    }

    /**
     * 获取所有岗位的分页列表
     */
    @Cacheable(value = "positions", key = "'all_' + #currentPage + '_' + #pageSize")
    public IPage<Position> getAllPositionsPaged(Integer currentPage, Integer pageSize) {
        Page<Position> page = new Page<>(currentPage, pageSize);
        return positionMapper.selectPage(page, null); // 使用BaseMapper的通用分页方法
    }

    /**
     * 根据ID删除岗位（软删除）
     */
    @CacheEvict(value = "positions", allEntries = true)
    public String deletePositionById(Integer positionId){
        int rows = positionMapper.deleteById(positionId);
        return rows > 0 ? "删除岗位成功" : "删除岗位失败";
    }

    /**
     * 创建新岗位
     */
    @CacheEvict(value = "positions", allEntries = true)
    public String createPosition(String employerAccount, String salaryRange, String jobDescription, String workLocation, String experienceRequirement, String educationRequirement, String universityName) {
        // 参数验证
        if (employerAccount == null || employerAccount.trim().isEmpty()) {
            return "雇主账号不能为空";
        }
        if (jobDescription == null || jobDescription.trim().isEmpty()) {
            return "岗位描述不能为空";
        }
        
        Position position = createPositionObject(employerAccount, salaryRange, jobDescription, workLocation, experienceRequirement, educationRequirement, universityName);
        position.setStatus(1); // 默认有效状态
        int rows = positionMapper.insertPosition(position);
        return rows > 0 ? "添加岗位成功" : "添加岗位失败";
    }

    /**
     * 更新岗位信息
     */
    @CacheEvict(value = "positions", allEntries = true)
    public String updatePositionById(Integer id, String employerAccount, String salaryRange, String jobDescription, String workLocation, String experienceRequirement, String educationRequirement, String universityName) {
        // 参数验证
        if (jobDescription == null || jobDescription.trim().isEmpty()) {
            return "岗位描述不能为空";
        }
        
        Position position = createPositionObject(employerAccount, salaryRange, jobDescription, workLocation, experienceRequirement, educationRequirement, universityName);
        position.setId(id);  // 设置ID用于更新条件
        position.setStatus(1); // 保持有效状态
        int rows = positionMapper.updatePosition(position);
        return rows > 0 ? "更新岗位成功" : "更新岗位失败";
    }
    
    /**
     * 获取随机岗位（用于推荐）
     */
    public List<Position> getRandomPositions() {
        return positionMapper.selectRandomPositions();
    }
    
    /**
     * 统计企业有效岗位数量
     */
    public int countActivePositionsByEmployer(String employerAccount) {
        return positionMapper.countActivePositions(employerAccount);
    }
    
    /**
     * 创建岗位对象的私有辅助方法
     */
    private Position createPositionObject(String employerAccount, String salaryRange, String jobDescription, String workLocation, String experienceRequirement, String educationRequirement, String universityName) {
        Position position = new Position();
        position.setEmployerAccount(employerAccount);
        position.setSalaryRange(salaryRange);
        position.setJobDescription(jobDescription);
        position.setWorkLocation(workLocation);
        position.setExperienceRequirement(experienceRequirement);
        position.setEducationRequirement(educationRequirement);
        position.setUniversityName(universityName); // 大学名称，为空表示校外岗位
        return position;
    }
}