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
import java.util.List;

/**
 * 简历投递服务类
 * 专注于简历投递记录的CRUD操作
 */
@Service
public class ResumeDeliveryService {

    @Autowired
    private ResumeDeliveryMapper resumeDeliveryMapper;
    
    @Autowired
    private PositionMapper positionMapper;
    
    @Autowired
    private EmployeeMapper employeeMapper;
    
    @Autowired
    private UserLoginMapper userLoginMapper;

    /**
     * 添加简历投递记录
     * 包含重复投递检查
     */
    public String addResumeDelivery(String userAccount, Integer positionId) {
        // 参数验证
        if (userAccount == null || userAccount.trim().isEmpty()) {
            return "用户账号不能为空";
        }
        if (positionId == null) {
            return "岗位ID不能为空";
        }
        
        // 检查是否已存在投递记录
        int exists = resumeDeliveryMapper.checkDeliveryExists(userAccount, positionId);
        if (exists > 0) {
            return "您已投递过该岗位";
        }
        
        ResumeDelivery resumeDelivery = new ResumeDelivery();
        resumeDelivery.setUserAccount(userAccount);
        resumeDelivery.setPositionId(positionId);
        
        int rows = resumeDeliveryMapper.insertResumeDelivery(resumeDelivery);
        return rows > 0 ? "投递成功" : "投递失败";
    }

    /**
     * 根据用户账号查询投递的岗位详情列表
     * 实现：先通过用户账号查询岗位ID，再通过岗位ID获取岗位详细信息
     */
    public List<Position> getPositionsByUserAccount(String userAccount) {
        if (userAccount == null || userAccount.trim().isEmpty()) {
            return null;
        }
        
        // 第一步：根据用户账号查询投递的岗位ID列表
        List<Integer> positionIds = resumeDeliveryMapper.selectPositionIdsByUserAccount(userAccount);
        if (positionIds == null || positionIds.isEmpty()) {
            return new java.util.ArrayList<>();
        }
        
        // 第二步：根据岗位ID查询岗位详细信息
        List<Position> positions = new java.util.ArrayList<>();
        for (Integer positionId : positionIds) {
            Position position = positionMapper.selectById(positionId);
            if (position != null) {
                positions.add(position);
            }
        }
        
        return positions;
    }

    /**
     * 根据岗位ID查询投递用户的简历详情列表
     * 实现：直接通过岗位ID查询未被拒绝的投递记录，再获取简历详细信息
     */
    public List<Employee> getEmployeesByPositionId(Integer positionId) {
        if (positionId == null) {
            return null;
        }
        
        // 第一步：根据岗位ID查询未被拒绝的投递用户账号列表
        List<String> userAccounts = resumeDeliveryMapper.selectNonRejectedUserAccountsByPositionId(positionId);
        if (userAccounts == null || userAccounts.isEmpty()) {
            return new java.util.ArrayList<>();
        }
        
        // 第二步：根据用户账号查询简历详情
        List<Employee> employees = new java.util.ArrayList<>();
        for (String userAccount : userAccounts) {
            Employee employee = employeeMapper.selectEmployee(userAccount);
            if (employee != null) {
                employees.add(employee);
            }
        }
        
        return employees;
    }

    /**
     * 修改投递状态为已拒绝(0)
     * 规则：任何时候都可以拒绝
     * @param userAccount 用户账号
     * @param positionId 岗位ID
     * @return 操作结果消息
     */
    public String updateStatusToRejected(String userAccount, Integer positionId) {
        // 参数验证
        if (userAccount == null || userAccount.trim().isEmpty()) {
            return "用户账号不能为空";
        }
        if (positionId == null) {
            return "岗位ID不能为空";
        }
        
        // 检查记录是否存在
        int exists = resumeDeliveryMapper.checkDeliveryExists(userAccount, positionId);
        if (exists <= 0) {
            return "状态更新失败：投递记录不存在";
        }
        
        int rows = resumeDeliveryMapper.updateStatusToRejected(userAccount, positionId);
        return rows > 0 ? "已拒绝该投递申请" : "无法拒绝：当前状态不支持此操作";
    }

    /**
     * 修改投递状态为邀约面试(2)
     * 规则：只能从未处理状态邀约
     * @param userAccount 用户账号
     * @param positionId 岗位ID
     * @return 操作结果消息
     */
    public String updateStatusToInterview(String userAccount, Integer positionId) {
        // 参数验证
        if (userAccount == null || userAccount.trim().isEmpty()) {
            return "用户账号不能为空";
        }
        if (positionId == null) {
            return "岗位ID不能为空";
        }
        
        // 检查记录是否存在
        int exists = resumeDeliveryMapper.checkDeliveryExists(userAccount, positionId);
        if (exists <= 0) {
            return "状态更新失败：投递记录不存在";
        }
        
        int rows = resumeDeliveryMapper.updateStatusToInterview(userAccount, positionId);
        return rows > 0 ? "已发送面试邀约" : "无法邀约：请先投递简历";
    }

    /**
     * 修改投递状态为面试通过(3)
     * 规则：只能从邀约面试状态转通过
     * @param userAccount 用户账号
     * @param positionId 岗位ID
     * @return 操作结果消息
     */
    public String updateStatusToPassed(String userAccount, Integer positionId) {
        // 参数验证
        if (userAccount == null || userAccount.trim().isEmpty()) {
            return "用户账号不能为空";
        }
        if (positionId == null) {
            return "岗位ID不能为空";
        }
        
        // 检查记录是否存在
        int exists = resumeDeliveryMapper.checkDeliveryExists(userAccount, positionId);
        if (exists <= 0) {
            return "状态更新失败：投递记录不存在";
        }
        
        int rows = resumeDeliveryMapper.updateStatusToPassed(userAccount, positionId);
        return rows > 0 ? "面试通过" : "无法标记通过：请先发送邀约";
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
        
        List<UserDeliveryVO> deliveries = resumeDeliveryMapper.selectDeliveriesWithPositionInfo(userAccount);
        
        // 为每个记录设置状态描述
        if (deliveries != null) {
            for (UserDeliveryVO delivery : deliveries) {
                delivery.setStatusDescByCode(delivery.getDeliveryStatus());
            }
        }
        
        return deliveries;
    }
}