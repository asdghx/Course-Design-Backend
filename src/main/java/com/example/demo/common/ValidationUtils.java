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
     * 验证邮箱格式
     */
    public static boolean isValidEmail(String email) {
        if (isEmpty(email)) return false;
        String regex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        return email.matches(regex);
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
     * 校验用户账号和密码参数（用于注册和登录）
     * @param userAccount 用户账号
     * @param userPassword 用户密码
     * @param userType 用户类型
     * @return 校验结果消息，null 表示校验通过
     */
    public static String validateUserParams(String userAccount, String userPassword, Integer userType) {
        // 账号校验
        if (isEmpty(userAccount)) {
            return "账号不能为空";
        }
        if (!isLengthBetween(userAccount, 6, 20)) {
            return "账号长度必须在 6-20 位之间";
        }
        
        // 密码校验
        if (isEmpty(userPassword)) {
            return "密码不能为空";
        }
        if (!isLengthBetween(userPassword, 6, 20)) {
            return "密码长度必须在 6-20 位之间";
        }
        
        // 用户类型校验
        if (!isValidUserType(userType)) {
            return "用户类型只能为" + com.example.demo.entity.UserLogin.USER_TYPE_PERSONAL + "(个人) 或" + com.example.demo.entity.UserLogin.USER_TYPE_ENTERPRISE + "(企业)";
        }
        
        return null; // 校验通过
    }
    
    /**
     * 校验岗位数据参数
     * @param jobDescription 岗位描述
     * @param salaryMin 最低薪资
     * @param salaryMax 最高薪资
     * @param workLocation 工作地点
     * @param latitude 纬度
     * @param longitude 经度
     * @return 校验结果消息，null 表示校验通过
     */
    public static String validatePositionData(String jobDescription, Integer salaryMin, Integer salaryMax, 
                                              String workLocation, Double latitude, Double longitude) {
        // 岗位描述校验
        if (jobDescription == null || jobDescription.trim().isEmpty()) {
            return "岗位描述不能为空";
        }
        
        // 薪资校验
        if (salaryMin != null && salaryMax != null) {
            if (salaryMin < 0) {
                return "最低工资不能为负数";
            }
            if (salaryMax < 0) {
                return "最高工资不能为负数";
            }
            if (salaryMin > salaryMax) {
                return "最低工资不能高于最高工资";
            }
            if (salaryMin > 1000000 || salaryMax > 1000000) {
                return "工资金额过高，请输入合理值";
            }
        }
        
        // 经纬度校验
        if (latitude != null && (latitude < -90 || latitude > 90)) {
            return "纬度必须在 -90 到 90 之间";
        }
        if (longitude != null && (longitude < -180 || longitude > 180)) {
            return "经度必须在 -180 到 180 之间";
        }
        
        return null; // 校验通过
    }
}