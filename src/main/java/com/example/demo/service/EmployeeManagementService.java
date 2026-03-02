package com.example.demo.service;

import com.example.demo.entity.Employee;
import com.example.demo.mapper.EmployeeMapper;
import com.example.demo.common.ValidationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
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
     * 获取员工信息（带缓存）
     */
    @Cacheable(value = "employee", key = "#userAccount")
    public Employee getEmployeeInfo(String userAccount){
        return employeeMapper.selectEmployee(userAccount);
    }

    /**
     * 保存或更新员工信息（upsert操作，清除缓存）
     */
    @CacheEvict(value = "employee", key = "#userAccount")
    public String upsertEmployee(String userAccount, String employeeName, String phoneNumber, String universityName, String jobIntention, String resume){
        // 参数验证
        if (ValidationUtils.isEmpty(userAccount)) {
            return "用户账号不能为空";
        }
        if (ValidationUtils.isEmpty(employeeName)) {
            return "员工姓名不能为空";
        }
        if (!ValidationUtils.isValidName(employeeName)) {
            return "姓名不能包含数字";
        }
        if (ValidationUtils.isEmpty(phoneNumber)) {
            return "手机号不能为空";
        }
        
        // 验证手机号格式
        if (!ValidationUtils.isValidPhone(phoneNumber)) {
            return "手机号格式不正确";
        }
        
        // 验证其他字段不能包含数字
        if (universityName != null && !ValidationUtils.isValidName(universityName)) {
            return "毕业院校不能包含数字";
        }
        
        // 检查手机号是否被其他用户使用
        int count = employeeMapper.countByPhoneNumberExceptSelf(phoneNumber, userAccount);
        if (count > 0) {
            return "该手机号已被其他用户使用";
        }
        
        int rows = employeeMapper.upsertEmployee(createEmployee(userAccount, employeeName, phoneNumber, universityName, jobIntention, resume));
        // 返回结果：如果影响1行表示插入新记录，影响2行表示更新现有记录
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