package com.example.demo.controller;

import com.example.demo.entity.Employee;
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
 * 处理用户相关的API请求
 */
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserLoginService userLoginService;

    @Autowired
    private EmployeeManagementService employeeManagementService;

    /**
     * 用户登录接口
     * 个人用户登录成功后返回Employee信息，企业用户返回基本登录信息
     */
    @PostMapping("/login")
    public Result<?> login(
            @RequestParam("userAccount") String userAccount,
            @RequestParam("userPassword") String userPassword,
            @RequestParam("userType") Integer userType
    ){
        // 1. 参数校验
        String errorMsg = ValidationUtils.validateLoginParams(userAccount, userPassword, userType);
        if (errorMsg != null) {
            return Result.badRequest(errorMsg);
        }
        
        // 2. 执行登录验证
        String loginResult = userLoginService.loginUser(userAccount, userPassword, userType);
        if (!loginResult.equals("登录成功")) {
            return Result.fail(loginResult);
        }
        
        // 3. 根据用户类型返回相应信息
        return buildLoginResponse(userAccount, userType);
    }

    /**
     * 用户注册接口
     */
    @PostMapping("/register")
    public Result<String> register(
            @RequestParam("userAccount") String userAccount,
            @RequestParam("userPassword") String userPassword,
            @RequestParam("userType") Integer userType
    ){
        // 1. 参数校验
        String errorMsg = ValidationUtils.validateRegisterParams(userAccount, userPassword, userType);
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
     * 根据用户类型返回相应的用户信息
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
        } else {
            // 企业用户返回基础登录信息
            UserLogin userLogin = userLoginService.getUserLoginByAccountAndType(userAccount, userType);
            return Result.ok(userLogin);
        }
    }

    /**
     * 获取员工信息接口
     * 用于修改个人信息后获取最新数据
     */
    @GetMapping("/employeeInfo")
    public Result<Employee> getEmployeeInfo(@RequestParam("userAccount") String userAccount){
        Employee employee = employeeManagementService.getEmployeeInfo(userAccount);
        return employee != null ? Result.ok(employee) : Result.fail("未找到该员工信息");
    }

    /**
     * 保存或更新员工信息接口（upsert操作）
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
}
