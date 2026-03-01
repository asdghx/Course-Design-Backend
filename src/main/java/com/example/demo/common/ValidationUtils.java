package com.example.demo.common;

/**
 * 参数校验工具类
 * 提供常用的参数校验方法，减少Controller层重复代码
 */
public class ValidationUtils {
    
    /**
     * 校验字符串是否为空或空白
     */
    public static boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }
    
    /**
     * 校验字符串长度是否在指定范围内
     */
    public static boolean isLengthValid(String str, int minLength, int maxLength) {
        if (isEmpty(str)) {
            return false;
        }
        int length = str.trim().length();
        return length >= minLength && length <= maxLength;
    }
    
    /**
     * 校验用户类型是否有效（1-个人用户，2-企业用户）
     */
    public static boolean isValidUserType(Integer userType) {
        return userType != null && (userType == com.example.demo.entity.UserLogin.USER_TYPE_PERSONAL || 
                                   userType == com.example.demo.entity.UserLogin.USER_TYPE_ENTERPRISE);
    }

    /**
     * 校验用户注册参数
     * @param userAccount 用户账号
     * @param userPassword 用户密码
     * @param userType 用户类型
     * @return 校验结果消息，null表示校验通过
     */
    public static String validateRegisterParams(String userAccount, String userPassword, Integer userType) {
        // 账号校验
        if (isEmpty(userAccount)) {
            return "账号不能为空";
        }
        if (!isLengthValid(userAccount, 6, 20)) {
            return "账号长度必须在6-20位之间";
        }
        
        // 密码校验
        if (isEmpty(userPassword)) {
            return "密码不能为空";
        }
        if (!isLengthValid(userPassword, 6, 20)) {
            return "密码长度必须在6-20位之间";
        }
        
        // 用户类型校验
        if (!isValidUserType(userType)) {
            return "用户类型只能为" + com.example.demo.entity.UserLogin.USER_TYPE_PERSONAL + "(个人)或" + com.example.demo.entity.UserLogin.USER_TYPE_ENTERPRISE + "(企业)";
        }
        
        return null; // 校验通过
    }

    /**
     * 校验用户登录参数
     * @param userAccount 用户账号
     * @param userPassword 用户密码
     * @param userType 用户类型
     * @return 校验结果消息，null表示校验通过
     */
    public static String validateLoginParams(String userAccount, String userPassword, Integer userType) {
        // 复用注册参数校验
        return validateRegisterParams(userAccount, userPassword, userType);
    }

}