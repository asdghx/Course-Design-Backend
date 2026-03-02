package com.example.demo.service;

import com.example.demo.entity.UserLogin;
import com.example.demo.mapper.UserLoginMapper;
import com.example.demo.common.MD5Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 用户登录服务类
 * 提供用户登录和注册功能
 */
@Service
public class UserLoginService {

    private final UserLoginMapper userLoginMapper;
    
    public UserLoginService(UserLoginMapper userLoginMapper) {
        this.userLoginMapper = userLoginMapper;
    }

    /**
     * 用户登录逻辑
     * 优化：只查询登录验证必需的字段
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
     * 用户注册逻辑
     * 优化：简化注册流程
     */
    public String registerUser(String userAccount, String userPassword, Integer userType) {
        // 检查用户是否已存在
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
        
        // 创建新用户
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
}