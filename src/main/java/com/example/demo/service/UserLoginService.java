package com.example.demo.service;

import com.example.demo.entity.UserLogin;
import com.example.demo.mapper.UserLoginMapper;
import com.example.demo.common.MD5Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * 用户登录服务类
 * 提供用户登录和注册功能
 */
@Service
public class UserLoginService {

    @Autowired
    private UserLoginMapper userLoginMapper;
    
    // 登录调试开关
    private static final boolean LOGIN_DEBUG = true;

    /**
     * 用户登录逻辑
     */
    @Cacheable(value = "userLogin", key = "#userAccount + '_' + #userType")
    public String loginUser(String userAccount, String userPassword, Integer userType) {
        if (LOGIN_DEBUG) {
            System.out.println("=== 开始用户登录验证 ===");
            System.out.println("查询用户: " + userAccount + ", 类型: " + userType);
        }
        
        UserLogin user = userLoginMapper.selectByAccountAndType(userAccount, userType);
        if (user == null) {
            if (LOGIN_DEBUG) System.out.println("用户不存在");
            return "账号不存在";
        }
        
        if (LOGIN_DEBUG) {
            System.out.println("找到用户, ID: " + user.getId() + ", 账号: " + user.getUserAccount());
        }
        
        // 检查账户状态
        if (user.getStatus() != null && user.getStatus() == 0) {
            if (LOGIN_DEBUG) System.out.println("账户状态异常, 状态码: " + user.getStatus());
            return "账户已被禁用";
        }
        
        // 使用盐值验证密码
        if (LOGIN_DEBUG) System.out.println("开始密码验证");
        if (!MD5Util.verifyWithSalt(userPassword, user.getUserPassword(), user.getSaltValue())) {
            if (LOGIN_DEBUG) System.out.println("密码验证失败");
            return "密码错误";
        }
        
        if (LOGIN_DEBUG) {
            System.out.println("登录验证成功, 用户ID: " + user.getId());
            System.out.println("=== 用户登录验证结束 ===");
        }
        return "登录成功";
    }

    /**
     * 用户注册逻辑
     */
    public String registerUser(String userAccount, String userPassword, Integer userType) {
        UserLogin user = userLoginMapper.selectByAccountAndType(userAccount, userType);
        if (user != null) {
            return "账号已存在";
        }
        // 密码和账号长度验证
        if(userPassword.length() < 6 || userPassword.length() > 20){
            return "密码长度必须在6-20位之间";
        }
        if(userAccount.length() < 6 || userAccount.length() > 20){
            return "账号长度必须在6-20位之间";
        }
        
        UserLogin newUser = new UserLogin();
        newUser.setUserAccount(userAccount);
        // 生成盐值并加密密码
        String saltValue = MD5Util.generateSalt();
        newUser.setSaltValue(saltValue);
        newUser.setUserPassword(MD5Util.encryptWithSalt(userPassword, saltValue));
        newUser.setUserType(userType);
        newUser.setStatus(1); // 默认启用状态
        
        int rows = userLoginMapper.insert(newUser);
        return rows > 0 ? "注册成功" : "注册失败";
    }

    /**
     * 禁用用户账户
     */
    @CacheEvict(value = "userLogin", key = "#userAccount + '_' + #userType")
    public String disableUserAccount(String userAccount, Integer userType) {
        int rows = userLoginMapper.updateStatusByAccount(userAccount, 0);
        return rows > 0 ? "账户禁用成功" : "账户禁用失败";
    }

    /**
     * 启用用户账户
     */
    @CacheEvict(value = "userLogin", key = "#userAccount + '_' + #userType")
    public String enableUserAccount(String userAccount, Integer userType) {
        int rows = userLoginMapper.updateStatusByAccount(userAccount, 1);
        return rows > 0 ? "账户启用成功" : "账户启用失败";
    }


}