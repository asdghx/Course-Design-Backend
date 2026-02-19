package com.example.demo.controller;

import com.example.demo.entity.Employee;
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

    @Autowired
    private PositionManagementService positionManagementService;

    @Autowired
    private CollaborativeFilteringRecommendationService collaborativeFilteringRecommendationService;

    /**
     * 用户登录接口
     */
    @PostMapping("/login")
    public Result<String> login(
            @RequestParam("userAccount") String userAccount,
            @RequestParam("userPassword") String userPassword,
            @RequestParam("userType") Integer userType
    ){
        Result<String> validationResult = validateLoginParams(userAccount, userPassword, userType);
        if (validationResult.getCode() != 200) {
            return validationResult;
        }
        
        String msg = userLoginService.loginUser(userAccount, userPassword, userType);

        return msg.equals("登录成功") ? Result.ok(msg) : Result.fail(msg);
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
        Result<String> validationResult = validateLoginParams(userAccount, userPassword, userType);
        if (validationResult.getCode() != 200) {
            return validationResult;
        }
        String msg = userLoginService.registerUser(userAccount, userPassword, userType);
        return msg.equals("注册成功") ? Result.ok(msg) : Result.fail(msg);
    }

    /**
     * 登录参数校验方法
     */
    private Result<String> validateLoginParams(String userAccount, String userPassword, Integer userType) {
        if (ValidationUtils.isEmpty(userAccount)) {
            return Result.badRequest("账号不能为空");
        }
        if (ValidationUtils.isEmpty(userPassword)) {
            return Result.badRequest("密码不能为空");
        }
        if (!ValidationUtils.isValidUserType(userType)) {
            return Result.badRequest("用户类型只能为" + com.example.demo.entity.UserLogin.USER_TYPE_PERSONAL + "(个人)或" + com.example.demo.entity.UserLogin.USER_TYPE_ENTERPRISE + "(企业)");
        }
        if (!ValidationUtils.isLengthValid(userAccount, 6, 20)) {
            return Result.badRequest("账号长度必须在6-20位之间");
        }
        if (!ValidationUtils.isLengthValid(userPassword, 6, 20)) {
            return Result.badRequest("密码长度必须在6-20位之间");
        }
        return Result.ok("参数校验通过");
    }

    /**
     * 获取员工信息接口
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