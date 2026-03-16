package com.example.demo.controller;

import com.example.demo.common.JwtUtil;
import com.example.demo.entity.Employee;
import com.example.demo.entity.Employer;
import com.example.demo.entity.UserLogin;
import com.example.demo.common.Result;
import com.example.demo.common.ValidationUtils;
import com.example.demo.service.*;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户控制器
 */
@RestController
@RequestMapping("/api/user")
@CrossOrigin("*")
public class UserController {
    private final EmployeeManagementService employeeManagementService;
    private final EmployerManagementService employerManagementService;
    private final UserLoginService userLoginService;
    private final JwtUtil jwtUtil;

    public UserController(
            EmployeeManagementService employeeManagementService,
            EmployerManagementService employerManagementService,
            UserLoginService userLoginService,
            JwtUtil jwtUtil) {
        this.employeeManagementService = employeeManagementService;
        this.employerManagementService = employerManagementService;
        this.userLoginService = userLoginService;
        this.jwtUtil = jwtUtil;
    }

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result<Map<String, Object>> login(
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
        String loginResult = userLoginService.loginUser(userAccount, userPassword, userType);
        if (!"登录成功".equals(loginResult)) {
            return Result.fail(loginResult);
        }
        
        // 3. 查询用户详细信息
        UserLogin user = userLoginService.getUserLoginByAccountAndType(userAccount, userType);
        if (user == null) {
            return Result.fail("用户不存在");
        }
        
        // 5. 生成 JWT Token
        String token = jwtUtil.createToken(user.getId(), user.getUserAccount(), user.getUserType());
        
        // 6. 根据用户类型返回详细信息
        Object detailInfo = null;
        if (userType == UserLogin.USER_TYPE_PERSONAL) {
            detailInfo = employeeManagementService.getEmployeeInfo(userAccount);
        } else if (userType == UserLogin.USER_TYPE_ENTERPRISE) {
            detailInfo = employerManagementService.getEmployerInfo(userAccount);
        }
        
        // 7. 构建响应数据（只返回 Token + 详细信息）
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("token", token);
        responseData.put("detailInfo", detailInfo);  // 详细的员工或企业信息
        
        return Result.ok(responseData);
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
     * 获取员工信息
     */
    @GetMapping("/employeeInfo")
    public Result<Employee> getEmployeeInfo(
            HttpServletRequest request
    ){
        // 从 Token 解析获取当前登录用户
        String userAccount = (String) request.getAttribute("currentUserAccount");
        if (userAccount == null) {
            return Result.fail("请先登录");
        }
        
        Employee employee = employeeManagementService.getEmployeeInfo(userAccount);
        return employee != null ? Result.ok(employee) : Result.fail("未找到该员工信息");
    }
    
    /**
     * 获取企业信息
     */
    @GetMapping("/employerInfo")
    public Result<Employer> getEmployerInfo(
            HttpServletRequest request
    ){
        System.out.println("\n========== [UserController] getEmployerInfo 调用开始 ==========");
        
        // 从 Token 解析获取当前登录用户
        String employerAccount = (String) request.getAttribute("currentUserAccount");
        System.out.println("【从 request 获取企业账号】");
        System.out.println("  employerAccount: " + employerAccount);
        
        if (employerAccount == null) {
            System.out.println("  ❌ employerAccount 为 null，返回 401");
            System.out.println("========== [UserController] getEmployerInfo 调用结束 - 返回 401 ==========");
            return Result.fail("请先登录");
        }
        
        System.out.println("  ✅ employerAccount: " + employerAccount);
        Employer employer = employerManagementService.getEmployerInfo(employerAccount);
        System.out.println("  查询企业信息返回：" + (employer != null ? "有数据" : "null"));
        System.out.println("========== [UserController] getEmployerInfo 调用结束 - 成功返回 ==========");
        
        // 如果没有企业信息，返回 null
        return Result.ok(employer);
    }
    
    /**
     * 保存或更新员工信息
     */
    @PostMapping("/upsertEmployee")
    public Result<String> upsertEmployee(
            HttpServletRequest request,
            @RequestParam("name") String name,
            @RequestParam("phone") String phone,
            @RequestParam("university") String university,
            @RequestParam("jobIntention") String jobIntention,
            @RequestParam("resume") String resume
    ){
        // 从 Token 解析获取当前登录用户
        String userAccount = (String) request.getAttribute("currentUserAccount");
        if (userAccount == null) {
            return Result.fail("请先登录");
        }
        
        // 参数验证
        String errorMsg = ValidationUtils.validateEmployeeParams(userAccount, name, phone, university, jobIntention, resume);
        if (errorMsg != null) {
            return Result.badRequest(errorMsg);
        }
        
        String msg = employeeManagementService.upsertEmployee(userAccount, name, phone, university, jobIntention, resume);
        return msg.equals("员工信息保存成功") ? Result.ok(msg) : Result.fail(msg);
    }
    
    /**
     * 保存或更新企业信息
     */
    @PostMapping("/upsertEmployer")
    public Result<String> upsertEmployer(
            HttpServletRequest request,
            @RequestParam("companyName") String companyName,
            @RequestParam("contactPhone") String contactPhone,
            @RequestParam(value = "contactEmail", required = false) String contactEmail,
            @RequestParam(value = "companyProfile", required = false) String companyProfile
    ){
        System.out.println("\n========== [UserController] upsertEmployer 调用开始 ==========");
        
        // 从 Token 解析获取当前登录用户
        String employerAccount = (String) request.getAttribute("currentUserAccount");
        System.out.println("【从 request 获取企业账号】");
        System.out.println("  employerAccount: " + employerAccount);
        
        if (employerAccount == null) {
            System.out.println("  ❌ employerAccount 为 null，返回 401");
            System.out.println("========== [UserController] upsertEmployer 调用结束 - 返回 401 ==========");
            return Result.fail("请先登录");
        }
        
        System.out.println("  ✅ employerAccount: " + employerAccount);
        System.out.println("  companyName: " + companyName);
        System.out.println("  contactPhone: " + contactPhone);
        System.out.println("  contactEmail: " + contactEmail);
        System.out.println("  companyProfile: " + (companyProfile != null ? "有数据" : "null"));
        
        // 参数验证
        System.out.println("\n【参数验证】");
        String errorMsg = ValidationUtils.validateEmployerParams(employerAccount, companyName, contactPhone, contactEmail, companyProfile);
        if (errorMsg != null) {
            System.out.println("  ❌ 验证失败：" + errorMsg);
            System.out.println("========== [UserController] upsertEmployer 调用结束 - 返回 400 ==========");
            return Result.badRequest(errorMsg);
        }
        System.out.println("  ✅ 参数验证通过");
        
        // 调用 Service
        System.out.println("\n【调用 Service】");
        String msg = employerManagementService.upsertEmployer(
            employerAccount, 
            companyName,
            contactPhone,
            contactEmail,
            companyProfile
        );
        System.out.println("  Service 返回：" + msg);
        
        System.out.println("========== [UserController] upsertEmployer 调用结束 ==========");
        return msg.equals("企业信息保存成功") ? Result.ok(msg) : Result.fail(msg);
    }

    /**
     * 修改密码（第二步）
     */
    @PostMapping("/changePassword")
    public Result<String> changePassword(
            @RequestParam("verifyCode") String verifyCode,
            @RequestParam("newPassword") String newPassword,
            @RequestParam("confirmPassword") String confirmPassword,
            @RequestParam("userAccount") String userAccount
    ){
        System.out.println("\n========== [UserController] changePassword 调用开始 ==========");
        System.out.println("  verifyCode: " + verifyCode);
        System.out.println("  newPassword: " + newPassword);
        System.out.println("  confirmPassword: " + confirmPassword);
        System.out.println("  userAccount: " + userAccount);
        
        // 调用 Service 修改密码
        String result = userLoginService.changePassword(verifyCode, newPassword, confirmPassword, userAccount);
        
        if ("密码修改成功".equals(result)) {
            System.out.println("========== [UserController] changePassword 调用结束 - 成功 ==========");
            return Result.ok(result);
        } else {
            System.out.println("========== [UserController] changePassword 调用结束 - 失败 ==========");
            return Result.fail(result);
        }
    }

    /**
     * 发送验证码（修改密码第一步）
     */
    @PostMapping("/sendVerifyCode")
    public Result<Map<String, String>> sendVerifyCode(
            @RequestParam("phone") String phone,
            @RequestParam("userAccount") String userAccount
    ){
        System.out.println("\n========== [UserController] sendVerifyCode 调用开始 ==========");
        System.out.println("  phone: " + phone);
        System.out.println("  userAccount: " + userAccount);
        
        // 调用 Service 验证并生成验证码
        Map<String, String> result = userLoginService.sendVerifyCode(phone, userAccount);
        
        if (result == null) {
            System.out.println("  ❌ 验证失败：账号或手机号不匹配");
            System.out.println("========== [UserController] sendVerifyCode 调用结束 - 返回失败 ==========");
            return Result.fail("账号或手机号不匹配");
        }
        
        System.out.println("  ✅ 验证码生成成功：" + result.get("verifyCode"));
        System.out.println("========== [UserController] sendVerifyCode 调用结束 - 返回成功 ==========");
        
        return Result.ok(result);
    }
}
