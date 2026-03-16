package com.example.demo.service;

import com.example.demo.entity.Employee;
import com.example.demo.mapper.EmployeeMapper;
import com.example.demo.common.ValidationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 员工信息服务类
 * 专注于员工信息的CRUD操作
 */
@Service
public class EmployeeManagementService {

    private final EmployeeMapper employeeMapper;
    
    public EmployeeManagementService(EmployeeMapper employeeMapper) {
        this.employeeMapper = employeeMapper;
    }

    /**
     * 获取员工信息
     */
    public Employee getEmployeeInfo(String userAccount){
        return employeeMapper.selectEmployee(userAccount);
    }

    /**
     * 保存或更新员工信息（upsert 操作）
     */
    public String upsertEmployee(String userAccount, String employeeName, String phoneNumber, String universityName, String jobIntention, String resume){
        // 参数验证
        String errorMsg = ValidationUtils.validateEmployeeParams(userAccount, employeeName, phoneNumber, universityName, jobIntention, resume);
        if (errorMsg != null) {
            return errorMsg;
        }
            
        // 检查手机号是否被其他用户使用
        int count = employeeMapper.countByPhoneNumberExceptSelf(phoneNumber, userAccount);
        if (count > 0) {
            return "该手机号已被其他用户使用";
        }
            
        int rows = employeeMapper.upsertEmployee(createEmployee(userAccount, employeeName, phoneNumber, universityName, jobIntention, resume));
        // 返回结果：如果影响 1 行表示插入新记录，影响 2 行表示更新现有记录
        return rows > 0 ? "员工信息保存成功" : "员工信息保存失败";
    }

    /**
     * 创建员工对象的私有辅助方法
     */
    private Employee createEmployee(String userAccount, String employeeName, String phoneNumber, String universityName, String jobIntention, String resume) {
        Employee employee = new Employee();
        employee.setUserAccount(userAccount);
        employee.setEmployeeName(employeeName);
        employee.setPhoneNumber(phoneNumber);
        employee.setUniversityName(universityName);
        employee.setJobIntention(jobIntention);
        employee.setResume(resume);
        return employee;
    }
}