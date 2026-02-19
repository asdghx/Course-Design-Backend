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
     */
    public String addResumeDelivery(String userAccount, Long positionId) {
        // 参数验证
        if (userAccount == null || userAccount.trim().isEmpty()) {
            return "用户账号不能为空";
        }
        if (positionId == null) {
            return "岗位ID不能为空";
        }
        
        ResumeDelivery resumeDelivery = new ResumeDelivery();
        resumeDelivery.setUserAccount(userAccount);
        resumeDelivery.setPositionId(positionId);
        
        int rows = resumeDeliveryMapper.insertResumeDelivery(resumeDelivery);
        return rows > 0 ? "简历投递成功" : "简历投递失败";
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
     * 实现：直接通过岗位ID查询用户账号，再获取简历详细信息
     */
    public List<Employee> getEmployeesByPositionId(Long positionId) {
        if (positionId == null) {
            return null;
        }
        
        // 第一步：根据岗位ID查询投递的用户账号列表
        List<String> userAccounts = resumeDeliveryMapper.selectUserAccountsByPositionId(positionId);
        if (userAccounts == null || userAccounts.isEmpty()) {
            return new java.util.ArrayList<>();
        }
        
        // 第二步：根据用户账号查询简历详情
        List<Employee> employees = new java.util.ArrayList<>();
        for (String userAccount : userAccounts) {
            Employee employee = employeeMapper.selectEmployee(userAccount);
            System.out.println(employee.getEmployeeName());
            if (employee != null) {
                employees.add(employee);
            }
        }
        
        return employees;
    }
}