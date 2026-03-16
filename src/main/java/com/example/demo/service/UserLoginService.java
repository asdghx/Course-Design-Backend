package com.example.demo.service;

import com.example.demo.entity.Employee;
import com.example.demo.entity.Employer;
import com.example.demo.entity.UserLogin;
import com.example.demo.mapper.EmployeeMapper;
import com.example.demo.mapper.EmployerMapper;
import com.example.demo.mapper.UserLoginMapper;
import com.example.demo.common.MD5Util;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * 用户登录服务
 */
@Service
public class UserLoginService {

    private final UserLoginMapper userLoginMapper;
    private final EmployeeMapper employeeMapper;
    private final EmployerMapper employerMapper;
    
    public UserLoginService(UserLoginMapper userLoginMapper, EmployeeMapper employeeMapper, EmployerMapper employerMapper) {
        this.userLoginMapper = userLoginMapper;
        this.employeeMapper = employeeMapper;
        this.employerMapper = employerMapper;
    }

    /**
     * 用户登录
     * @return "登录成功"、"账号不存在"或"密码错误"
     */
    public String loginUser(String userAccount, String userPassword, Integer userType) {
        // 登录验证只需要账号、密码、盐值、用户类型
        UserLogin user = userLoginMapper.selectByAccountAndType(userAccount, userType);
        if (user == null) {
            return "账号不存在";
        }
        // 使用盐值验证密码
        if (!MD5Util.verifyWithSalt(userPassword, user.getUserPassword(), user.getSaltValue())) {
            return "密码错误";
        }
        return "登录成功";
    }

    /**
     * 用户注册
     * @return "注册成功"或"账号已存在"
     */
    public String registerUser(String userAccount, String userPassword, Integer userType) {
        // 检查账号是否已存在
        if (userLoginMapper.selectByAccountAndType(userAccount, userType) != null) {
            return "账号已存在";
        }
        
        // 密码和账号长度验证
        if(userPassword.length() < 6 || userPassword.length() > 20){
            return "密码长度必须在6-20位之间";
        }
        if(userAccount.length() < 6 || userAccount.length() > 20){
            return "账号长度必须在6-20位之间";
        }
        
        // 创建新用户并加密密码
        UserLogin newUser = new UserLogin();
        newUser.setUserAccount(userAccount);
        // 生成盐值并加密密码
        String saltValue = MD5Util.generateSalt();
        newUser.setSaltValue(saltValue);
        newUser.setUserPassword(MD5Util.encryptWithSalt(userPassword, saltValue));
        newUser.setUserType(userType);
        
        int rows = userLoginMapper.insert(newUser);
        return rows > 0 ? "注册成功" : "注册失败";
    }

    /**
     * 根据账号和类型获取用户登录信息
     */
    public UserLogin getUserLoginByAccountAndType(String userAccount, Integer userType) {
        return userLoginMapper.selectByAccountAndType(userAccount, userType);
    }

    /**
     * 发送验证码（修改密码第一步）
     * @return 包含验证码和用户账号的 Map，如果验证失败返回 null
     */
    public Map<String, String> sendVerifyCode(String phone, String userAccount) {
        // 1. 查询用户信息（不限制类型）
        UserLogin user = userLoginMapper.selectByAccount(userAccount);
        if (user == null) {
            System.out.println("  ❌ 账号不存在：" + userAccount);
            return null;
        }
        
        // 2. 根据用户类型查询对应的手机号
        Integer userType = user.getUserType();
        String storedPhone = null;
        
        if (userType == UserLogin.USER_TYPE_PERSONAL) {
            // 个人用户：查询 employee 表
            storedPhone = employeeMapper.selectPhoneNumberByAccount(userAccount);
            System.out.println("  📱 个人用户，从 employee 表查询手机号：" + storedPhone);
        } else if (userType == UserLogin.USER_TYPE_ENTERPRISE) {
            // 企业用户：查询 employer 表
            storedPhone = employerMapper.selectContactPhoneByAccount(userAccount);
            System.out.println("  🏢 企业用户，从 employer 表查询手机号：" + storedPhone);
        }
        
        // 3. 验证手机号是否匹配
        if (storedPhone == null || !storedPhone.equals(phone)) {
            System.out.println("  ❌ 手机号不匹配 - 输入的：" + phone + ", 数据库的：" + storedPhone);
            return null;
        }
        
        System.out.println("  ✅ 账号和手机号验证通过");
        
        // 4. 返回固定验证码和用户账号
        Map<String, String> result = new HashMap<>();
        result.put("verifyCode", "123456");
        result.put("userAccount", userAccount); // 返回账号，用于第二步
        
        return result;
    }

    /**
     * 修改密码（第二步）
     * @param verifyCode 验证码
     * @param newPassword 新密码
     * @param confirmPassword 确认密码
     * @param userAccount 用户账号
     * @return 修改结果消息
     */
    public String changePassword(String verifyCode, String newPassword, String confirmPassword, String userAccount) {
        // 1. 验证两次密码是否一致
        if (!newPassword.equals(confirmPassword)) {
            System.out.println("  ❌ 两次密码不一致");
            return "两次输入的密码不一致";
        }
        
        // 2. 验证密码长度
        if (newPassword.length() < 6 || newPassword.length() > 20) {
            System.out.println("  ❌ 密码长度必须在 6-20 位之间");
            return "密码长度必须在 6-20 位之间";
        }
        
        // 3. 验证验证码
        if (!"123456".equals(verifyCode)) {
            System.out.println("  ❌ 验证码错误：" + verifyCode);
            return "验证码错误";
        }
        
        System.out.println("  ✅ 验证码和密码验证通过");
        
        // 4. 生成盐值并加密密码
        String saltValue = MD5Util.generateSalt();
        String encryptedPassword = MD5Util.encryptWithSalt(newPassword, saltValue);
        
        // 5. 更新数据库密码
        int rows = userLoginMapper.updatePassword(userAccount, encryptedPassword, saltValue);
        
        if (rows > 0) {
            System.out.println("  ✅ 密码修改成功，已更新到数据库");
            return "密码修改成功";
        } else {
            System.out.println("  ❌ 密码修改失败");
            return "密码修改失败";
        }
    }
}