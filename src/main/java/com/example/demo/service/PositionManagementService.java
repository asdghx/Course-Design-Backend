package com.example.demo.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.common.PageResultBuilder;
import com.example.demo.common.PositionVOConverter;
import com.example.demo.entity.Position;
import com.example.demo.entity.vo.PositionVO;
import com.example.demo.mapper.PositionMapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 岗位管理服务类
 * 专注于岗位的CRUD操作
 */
@Service
public class PositionManagementService {
    private final PositionMapper positionMapper;
    private final PositionVOConverter positionVOConverter;
    public PositionManagementService(PositionMapper positionMapper, PositionVOConverter positionVOConverter) {
        this.positionMapper = positionMapper;
        this.positionVOConverter = positionVOConverter;
    }

    /**
     * 根据企业账号分页查询岗位列表（HR 管理专用，不限制学校和状态）
     * @param employerAccount 企业账号（可选，为 null 时查询所有企业的岗位）
     * @param currentPage 当前页码
     * @param pageSize 每页大小
     * @return 岗位分页结果
     */
    public IPage<PositionVO> getPositionListByEmployerAccountWithPagination(String employerAccount, Integer currentPage, Integer pageSize) {
        Page<Position> page = new Page<>(currentPage, pageSize);
        IPage<Position> positionPage = positionMapper.selectPositionsDynamic(page, employerAccount);
        
        // 转换为 VO 列表
        List<PositionVO> voList = positionVOConverter.convertFromPageRecords(positionPage);
        
        // 构造返回结果
        return PageResultBuilder.build(page, voList);
    }

    /**
     * 获取所有岗位的分页列表
     */
    public IPage<PositionVO> getAllPositionsPaged(Integer currentPage, Integer pageSize) {
        // 复用通用查询方法，传入 null 表示查询所有岗位
        return getPositionListByEmployerAccountWithPagination(null, currentPage, pageSize);
    }

    /**
     * 创建新岗位
     */
    public String createPosition(String employerAccount, Integer salaryMin, Integer salaryMax, String jobDescription, String workLocation, String universityName, Double latitude, Double longitude) {
        Position position = createPositionObject(employerAccount, salaryMin, salaryMax, jobDescription, workLocation, universityName, latitude, longitude);
        position.setStatus(1); // 默认有效状态
        int rows = positionMapper.upsertPosition(position);
        return rows > 0 ? "添加岗位成功" : "添加岗位失败";
    }

    /**
     * 更新岗位信息
     */
    public String updatePositionById(Integer id, Integer salaryMin, Integer salaryMax, String jobDescription, String workLocation, String universityName, Double latitude, Double longitude) {
        Position position = createPositionObject(null, salaryMin, salaryMax, jobDescription, workLocation, universityName, latitude, longitude);
        position.setId(id);  // 设置 ID 用于 UPSERT
        int rows = positionMapper.upsertPosition(position);
        return rows > 0 ? "更新岗位成功" : "更新岗位失败";
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
        int rows = positionMapper.upsertPosition(position);
        
        return rows > 0 ? "删除岗位成功" : "删除岗位失败";
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
    

}