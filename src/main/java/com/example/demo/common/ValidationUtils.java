package com.example.demo.common;

/**
 * 通用参数验证工具类
 * 提供各种业务参数的验证方法
 */
public class ValidationUtils {
    
    /**
     * 验证字符串是否为空或空白
     */
    public static boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }
    
    /**
     * 验证姓名格式（不能包含数字）
     */
    public static boolean isValidName(String name) {
        return !isEmpty(name) && !name.matches(".*\\d+.*");
    }
    
    /**
     * 验证手机号格式
     */
    public static boolean isValidPhone(String phone) {
        return !isEmpty(phone) && phone.matches("^[1][3-9][0-9]{9}$");
    }
    
    /**
     * 验证字符串长度范围
     */
    public static boolean isLengthBetween(String str, int min, int max) {
        if (str == null) return false;
        int len = str.length();
        return len >= min && len <= max;
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
        if (!isLengthBetween(userAccount, 6, 20)) {
            return "账号长度必须在6-20位之间";
        }
        
        // 密码校验
        if (isEmpty(userPassword)) {
            return "密码不能为空";
        }
        if (!isLengthBetween(userPassword, 6, 20)) {
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