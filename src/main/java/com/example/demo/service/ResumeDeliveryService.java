package com.example.demo.service;

import com.example.demo.entity.Employee;
import com.example.demo.entity.Position;
import com.example.demo.entity.ResumeDelivery;
import com.example.demo.entity.UserLogin;
import com.example.demo.mapper.EmployeeMapper;
import com.example.demo.mapper.PositionMapper;
import com.example.demo.mapper.ResumeDeliveryMapper;
import com.example.demo.mapper.UserLoginMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entity.vo.UserDeliveryVO;
import java.util.ArrayList;
import java.util.List;

/**
 * 简历投递服务类
 * 专注于简历投递记录的CRUD操作
 */
@Service
public class ResumeDeliveryService {

    private final ResumeDeliveryMapper resumeDeliveryMapper;
    private final PositionMapper positionMapper;
    private final EmployeeMapper employeeMapper;
    private final UserPositionHistoryService userPositionHistoryService;
    
    public ResumeDeliveryService(
            ResumeDeliveryMapper resumeDeliveryMapper,
            PositionMapper positionMapper,
            EmployeeMapper employeeMapper,
            UserPositionHistoryService userPositionHistoryService) {
        this.resumeDeliveryMapper = resumeDeliveryMapper;
        this.positionMapper = positionMapper;
        this.employeeMapper = employeeMapper;
        this.userPositionHistoryService = userPositionHistoryService;
    }

    /**
     * 添加简历投递记录
     * 包含重复投递检查
     */
    public String addResumeDelivery(String userAccount, Integer positionId) {
        // 检查是否已存在投递记录
        int exists = resumeDeliveryMapper.checkDeliveryExists(userAccount, positionId);
        if (exists > 0) {
            return "您已投递过该岗位";
        }
            
        ResumeDelivery resumeDelivery = new ResumeDelivery();
        resumeDelivery.setUserAccount(userAccount);
        resumeDelivery.setPositionId(positionId);
        resumeDelivery.setHasUpdate(1);  // 初始投递时设置为 1，提醒企业处理
            
        int rows = resumeDeliveryMapper.insertResumeDelivery(resumeDelivery);
        
        // 投递成功后自动记录投递历史
        if (rows > 0) {
            userPositionHistoryService.recordPositionDelivery(userAccount, positionId);
        }
        
        return rows > 0 ? "投递成功" : "投递失败";
    }

    /**
     * 根据用户账号查询投递的岗位详情列表
     * 优化：使用批量查询避免 N+1 问题
     */
    public List<Position> getPositionsByUserAccount(String userAccount) {
        if (userAccount == null || userAccount.trim().isEmpty()) {
            return null;
        }
            
        // 第一步：根据用户账号查询投递的岗位 ID 列表
        List<Integer> positionIds = resumeDeliveryMapper.selectPositionIdsByUserAccount(userAccount);
        if (positionIds == null || positionIds.isEmpty()) {
            return new ArrayList<>();
        }
            
        // 第二步：批量查询岗位信息（一次查询，避免 N+1 问题）
        return positionMapper.selectBatchIds(positionIds);
    }

    /**
     * 根据岗位 ID 查询投递用户的简历详情列表
     * 优化：使用批量查询避免 N+1 问题
     */
    public List<Employee> getEmployeesByPositionId(Integer positionId) {
        if (positionId == null) {
            return null;
        }
            
        // 第一步：根据岗位 ID 查询未被拒绝的投递用户账号列表
        List<String> userAccounts = resumeDeliveryMapper.selectNonRejectedUserAccountsByPositionId(positionId);
        if (userAccounts == null || userAccounts.isEmpty()) {
            return new ArrayList<>();
        }
            
        // 第二步：批量查询员工信息（一次查询，避免 N+1 问题）
        return employeeMapper.selectBatchByUserAccounts(userAccounts);
    }

    /**
     * 更新投递状态（统一接口）
     * @param userAccount 用户账号
     * @param positionId 岗位 ID
     * @param status 目标状态（0-拒绝，2-邀约面试，3-面试通过，4-已入职）
     * @return 操作结果消息
     */
  public String updateDeliveryStatus(String userAccount, Integer positionId, Integer status) {
        // 1. 检查记录是否存在
        int exists = resumeDeliveryMapper.checkDeliveryExists(userAccount, positionId);
        if (exists <= 0) {
            return "状态更新失败：投递记录不存在";
        }
        
        // 2. 查询当前状态
        Integer currentStatus = resumeDeliveryMapper.getCurrentStatus(userAccount, positionId);
        if (currentStatus == null) {
            return "状态更新失败：无法获取当前状态";
        }
        
        // 3. 状态流转校验
        String flowError = validateStatusFlow(currentStatus, status);
        if (flowError != null) {
            return flowError;
        }
        
        // 4. 根据状态调用不同的 Mapper 方法
        int rows;
        switch (status) {
            case ResumeDelivery.STATUS_REJECTED:
                rows = resumeDeliveryMapper.updateStatusToRejected(userAccount, positionId);
                break;
            case ResumeDelivery.STATUS_INTERVIEW:
                rows = resumeDeliveryMapper.updateStatusToInterview(userAccount, positionId);
                // 邀约面试：记录投递历史
                userPositionHistoryService.recordPositionDelivery(userAccount, positionId);
                break;
            case ResumeDelivery.STATUS_PASSED:
                rows = resumeDeliveryMapper.updateStatusToPassed(userAccount, positionId);
                // 面试通过：记录通过历史
                userPositionHistoryService.recordPositionPass(userAccount, positionId);
                break;
            case ResumeDelivery.STATUS_HIRED:
                rows = resumeDeliveryMapper.updateStatusToHired(userAccount, positionId);
                break;
            default:
                return "不支持的状态值";
        }
        
        return rows > 0 ? "状态更新成功" : "状态更新失败：当前状态不支持此操作";
    }
    
    /**
     * 校验状态流转是否合法
     * @param currentStatus 当前状态
     * @param targetStatus 目标状态
     * @return 错误消息，null 表示校验通过
     */
    private String validateStatusFlow(Integer currentStatus, Integer targetStatus) {
        // 已拒绝状态不能再流转到其他状态（除了拒绝本身）
        if (currentStatus == ResumeDelivery.STATUS_REJECTED && targetStatus != ResumeDelivery.STATUS_REJECTED) {
            return "该投递已被拒绝，无法更改状态";
        }
        
        // 邀约面试：只能从已投递 (1) 状态流转
        if (targetStatus == ResumeDelivery.STATUS_INTERVIEW) {
            if (currentStatus != ResumeDelivery.STATUS_DELIVERED) {
                return "只有已投递的岗位才能邀约面试";
            }
        }
        
        // 面试通过：只能从邀约面试 (2) 状态流转
        if (targetStatus == ResumeDelivery.STATUS_PASSED) {
            if (currentStatus != ResumeDelivery.STATUS_INTERVIEW) {
                return "只有已邀约面试的岗位才能设置为面试通过";
            }
        }
        
        // 已入职：只能从面试通过 (3) 状态流转
        if (targetStatus == ResumeDelivery.STATUS_HIRED) {
            if (currentStatus != ResumeDelivery.STATUS_PASSED) {
                return "只有面试通过的岗位才能设置为已入职";
            }
        }
        
        return null; // 校验通过
    }

    /**
     * 删除投递记录
     * 规则：只能删除状态为 0（已拒绝）的记录
     * @param userAccount 用户账号
     * @param positionId 岗位 ID
     * @return 操作结果消息
     */
    public String deleteDelivery(String userAccount, Integer positionId) {
        // 1. 检查记录是否存在
        int exists = resumeDeliveryMapper.checkDeliveryExists(userAccount, positionId);
        if (exists <= 0) {
            return "删除失败：投递记录不存在";
        }
        
        // 2. 查询当前状态
        Integer currentStatus = resumeDeliveryMapper.getCurrentStatus(userAccount, positionId);
        if (currentStatus == null) {
            return "删除失败：无法获取当前状态";
        }
        
        // 3. 校验状态：只能删除已拒绝（0）的记录
        if (currentStatus != ResumeDelivery.STATUS_REJECTED) {
            return "只能删除已拒绝的投递记录";
        }
        
        // 4. 执行删除
        int rows = resumeDeliveryMapper.deleteDelivery(userAccount, positionId);
        return rows > 0 ? "删除成功" : "删除失败";
    }

    /**
     * 根据用户账号查询投递记录详情（包含岗位信息和投递状态）
     * @param userAccount 用户账号
     * @return 投递记录列表
     */
    public List<UserDeliveryVO> getDeliveriesWithPositionInfo(String userAccount) {
        if (userAccount == null || userAccount.trim().isEmpty()) {
            return new java.util.ArrayList<>();
        }
        
        // 1. 先查询投递记录（包含 has_update 字段）
        List<UserDeliveryVO> deliveries = resumeDeliveryMapper.selectDeliveriesWithPositionInfo(userAccount);
        
        // 2. 为每个记录设置状态描述
        if (deliveries != null) {
            for (UserDeliveryVO delivery : deliveries) {
                delivery.setStatusDescByCode(delivery.getDeliveryStatus());
            }
        }
        
        return deliveries;
    }

    /**
     * 将用户的所有投递记录标记为已读（has_update = 0）
     * @param userAccount 用户账号
     * @return 更新的记录数
     */
    public int markAllAsRead(String userAccount) {
        if (userAccount == null || userAccount.trim().isEmpty()) {
            return 0;
        }
        
        return resumeDeliveryMapper.markAsRead(userAccount);
    }

    /**
     * 统计用户有多少条未读更新（has_update = 1）
     * @param userAccount 用户账号
     * @return 未读更新数量
     */
    public int countUnreadUpdates(String userAccount) {
        if (userAccount == null || userAccount.trim().isEmpty()) {
            return 0;
        }
        
        return resumeDeliveryMapper.countUnreadUpdates(userAccount);
    }
}