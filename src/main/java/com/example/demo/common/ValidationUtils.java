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

}