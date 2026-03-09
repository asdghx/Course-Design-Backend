package com.example.demo.controller;

import com.example.demo.entity.Employee;
import com.example.demo.entity.Employer;
import com.example.demo.entity.UserLogin;
import com.example.demo.common.Result;
import com.example.demo.common.ValidationUtils;
import com.example.demo.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 用户控制器
 */
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserLoginService userLoginService;
    @Autowired
    private EmployeeManagementService employeeManagementService;
    @Autowired
    private EmployerManagementService employerManagementService;

    /**
     * 用户登录（个人用户返回 Employee，企业用户返回 Employer）
     */
    @PostMapping("/login")
    public Result<?> login(
            @RequestParam("userAccount") String userAccount,
            @RequestParam("userPassword") String userPassword,
            @RequestParam("userType") Integer userType
    ) {
        // 1. 参数校验
        String errorMsg = ValidationUtils.validateUserParams(userAccount, userPassword, userType);
        if (errorMsg != null) {
            return Result.badRequest(errorMsg);
        }
            
        // 2. 执行登录验证
        return buildLoginResponse(userAccount, userType);
    }

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public Result<String> register(
            @RequestParam("userAccount") String userAccount,
            @RequestParam("userPassword") String userPassword,
            @RequestParam("userType") Integer userType
    ) {
        // 1. 参数校验
        String errorMsg = ValidationUtils.validateUserParams(userAccount, userPassword, userType);
        if (errorMsg != null) {
            return Result.badRequest(errorMsg);
        }
            
        // 2. 执行注册
        String registerResult = userLoginService.registerUser(userAccount, userPassword, userType);
        
        // 3. 返回结果
        return registerResult.equals("注册成功") ? Result.ok(registerResult) : Result.fail(registerResult);
    }

    /**
     * 构建登录响应
     */
    private Result<?> buildLoginResponse(String userAccount, Integer userType) {
        if (userType == UserLogin.USER_TYPE_PERSONAL) {
            // 个人用户返回Employee信息
            Employee employee = employeeManagementService.getEmployeeInfo(userAccount);
            if (employee != null) {
                return Result.ok(employee);
            } else {
                // 如果没有员工信息，返回基础用户信息
                UserLogin userLogin = userLoginService.getUserLoginByAccountAndType(userAccount, userType);
                return Result.ok(userLogin);
            }
        } else if (userType == UserLogin.USER_TYPE_ENTERPRISE) {
            // 企业用户返回 Employer 信息
            Employer employer = employerManagementService.getEmployerInfo(userAccount);
            if (employer != null) {
                return Result.ok(employer);
            } else {
                // 如果没有企业信息，返回基础登录信息
                UserLogin userLogin = userLoginService.getUserLoginByAccountAndType(userAccount, userType);
                return Result.ok(userLogin);
            }
        } else {
            // 其他类型返回基础登录信息
            UserLogin userLogin = userLoginService.getUserLoginByAccountAndType(userAccount, userType);
            return Result.ok(userLogin);
        }
    }

    /**
     * 获取员工信息
     */
    @GetMapping("/employeeInfo")
    public Result<Employee> getEmployeeInfo(@RequestParam("userAccount") String userAccount){
        Employee employee = employeeManagementService.getEmployeeInfo(userAccount);
        return employee != null ? Result.ok(employee) : Result.fail("未找到该员工信息");
    }
    
    /**
     * 获取企业信息
     */
    @GetMapping("/employerInfo")
    public Result<Employer> getEmployerInfo(@RequestParam("employerAccount") String employerAccount){
        Employer employer = employerManagementService.getEmployerInfo(employerAccount);
        // 如果没有企业信息，返回 null
        return Result.ok(employer);
    }
    
    /**
     * 保存或更新员工信息
     */
    @PostMapping("/upsertEmployee")
    public Result<String> upsertEmployee(
            @RequestParam("userAccount") String userAccount,
            @RequestParam("name") String name,
            @RequestParam("phone") String phone,
            @RequestParam("university") String university,
            @RequestParam("jobIntention") String jobIntention,
            @RequestParam("resume") String resume
    ){
        String msg = employeeManagementService.upsertEmployee(userAccount, name, phone, university, jobIntention, resume);
        return msg.equals("员工信息保存成功") ? Result.ok(msg) : Result.fail(msg);
    }
    
    /**
     * 保存或更新企业信息
     */
    @PostMapping("/upsertEmployer")
    public Result<String> upsertEmployer(
            @RequestParam("employerAccount") String employerAccount,
            @RequestParam("companyName") String companyName,
            @RequestParam("contactPhone") String contactPhone,
            @RequestParam(value = "contactEmail", required = false) String contactEmail,
            @RequestParam(value = "companyProfile", required = false) String companyProfile
    ){
        String msg = employerManagementService.upsertEmployer(
            employerAccount, 
            companyName,
            contactPhone,
            contactEmail,
            companyProfile
        );
        return msg.equals("企业信息保存成功") ? Result.ok(msg) : Result.fail(msg);
    }
}
