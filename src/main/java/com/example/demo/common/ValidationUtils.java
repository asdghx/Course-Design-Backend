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
     */
    public static String validateUserParams(String userAccount, String userPassword, Integer userType) {
        // 验证账号
        String accountMsg = validateAccount(userAccount);
        if (accountMsg != null) return accountMsg;
        
        // 验证密码
        String passwordMsg = validatePassword(userPassword);
        if (passwordMsg != null) return passwordMsg;
        
        // 验证用户类型
        if (!isValidUserType(userType)) {
            return "用户类型只能为" + com.example.demo.entity.UserLogin.USER_TYPE_PERSONAL + "(个人) 或" + com.example.demo.entity.UserLogin.USER_TYPE_ENTERPRISE + "(企业)";
        }
        
        return null;
    }
    
    /**
     * 校验账号（6-20 位）
     */
    private static String validateAccount(String account) {
        if (isEmpty(account)) {
            return "账号不能为空";
        }
        if (!isLengthBetween(account, 6, 20)) {
            return "账号长度必须在 6-20 位之间";
        }
        return null;
    }
    
    /**
     * 校验密码（6-20 位）
     */
    private static String validatePassword(String password) {
        if (isEmpty(password)) {
            return "密码不能为空";
        }
        if (!isLengthBetween(password, 6, 20)) {
            return "密码长度必须在 6-20 位之间";
        }
        return null;
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
    
    /**
     * 校验员工信息参数（供 UserController.upsertEmployee 调用）
     */
    public static String validateEmployeeParams(String userAccount, String employeeName, String phoneNumber,
                                                 String universityName, String jobIntention, String resume) {
        // 验证账号
        String accountMsg = validateAccount(userAccount);
        if (accountMsg != null) return accountMsg;
        
        // 验证姓名
        String nameMsg = validateName(employeeName);
        if (nameMsg != null) return nameMsg;
        
        // 验证手机号
        String phoneMsg = validatePhone(phoneNumber);
        if (phoneMsg != null) return phoneMsg;
        
        // 验证院校名称（可选）
        if (universityName != null && !universityName.trim().isEmpty()) {
            String uniMsg = validateName(universityName);
            if (uniMsg != null) return uniMsg;
            if (!isLengthBetween(universityName, 2, 100)) {
                return "院校名称长度必须在 2-100 个字符之间";
            }
        }
        
        // 验证求职意向（可选）
        if (jobIntention != null && !jobIntention.trim().isEmpty()) {
            if (!isLengthBetween(jobIntention, 2, 200)) {
                return "求职意向长度必须在 2-200 个字符之间";
            }
        }
        
        // 验证简历内容（可选）
        if (resume != null && !resume.trim().isEmpty()) {
            if (!isLengthBetween(resume, 10, 2000)) {
                return "简历内容长度必须在 10-2000 个字符之间";
            }
        }
        
        return null;
    }
    
    /**
     * 校验姓名（不含数字，2-50 字）
     */
    private static String validateName(String name) {
        if (isEmpty(name)) {
            return "姓名不能为空";
        }
        if (!isLengthBetween(name, 2, 50)) {
            return "姓名长度必须在 2-50 个字符之间";
        }
        if (!isValidName(name)) {
            return "姓名不能包含数字";
        }
        return null;
    }
    
    /**
     * 校验企业信息参数（供 UserController.upsertEmployer 调用）
     */
    public static String validateEmployerParams(String employerAccount, String companyName, String contactPhone,
                                                 String contactEmail, String companyProfile) {
        // 验证账号
        String accountMsg = validateAccount(employerAccount);
        if (accountMsg != null) return accountMsg;
        
        // 验证企业名称
        String companyMsg = validateCompanyName(companyName);
        if (companyMsg != null) return companyMsg;
        
        // 验证联系电话
        String phoneMsg = validatePhone(contactPhone);
        if (phoneMsg != null) return phoneMsg;
        
        // 验证联系邮箱（可选）
        if (contactEmail != null && !contactEmail.trim().isEmpty()) {
            String emailMsg = validateEmail(contactEmail);
            if (emailMsg != null) return emailMsg;
        }
        
        // 验证企业简介（可选）
        if (companyProfile != null && !companyProfile.trim().isEmpty()) {
            if (!isLengthBetween(companyProfile, 10, 2000)) {
                return "企业简介长度必须在 10-2000 个字符之间";
            }
        }
        
        return null;
    }
    
    /**
     * 校验企业名称（2-200 字，可含数字）
     */
    private static String validateCompanyName(String companyName) {
        if (isEmpty(companyName)) {
            return "企业名称不能为空";
        }
        if (!isLengthBetween(companyName, 2, 200)) {
            return "企业名称长度必须在 2-200 个字符之间";
        }
        return null;
    }
    
    /**
     * 校验手机号（11 位格式）
     */
    private static String validatePhone(String phone) {
        if (isEmpty(phone)) {
            return "手机号不能为空";
        }
        if (!isValidPhone(phone)) {
            return "手机号格式不正确";
        }
        return null;
    }
    
    /**
     * 校验邮箱格式
     */
    private static String validateEmail(String email) {
        if (!isValidEmail(email)) {
            return "邮箱格式不正确";
        }
        if (!isLengthBetween(email, 5, 100)) {
            return "邮箱地址长度必须在 5-100 个字符之间";
        }
        return null;
    }
    
    /**
     * 校验收藏相关参数
     * @param articleId 文章 ID
     * @return 校验结果消息，null 表示校验通过
     */
    public static String validateCollectionParams(Integer articleId) {
        if (articleId == null) {
            return "文章 ID 不能为空";
        }
        if (articleId <= 0) {
            return "文章 ID 必须为正整数";
        }
        return null;
    }
    
    /**
     * 校验搜索收藏文章参数
     * @param userAccount 用户账号
     * @param keyword 搜索关键词
     * @return 校验结果消息，null 表示校验通过
     */
    public static String validateSearchCollectedParams(String userAccount, String keyword) {
        if (isEmpty(userAccount)) {
            return "用户账号不能为空";
        }
        if (isEmpty(keyword)) {
            return "搜索关键词不能为空";
        }
        return null;
    }
    
    /**
     * 校验简历投递参数
     * @param positionId 岗位 ID
     * @return 校验结果消息，null 表示校验通过
     */
    public static String validateResumeDeliveryParams(Integer positionId) {
        if (positionId == null) {
            return "岗位 ID 不能为空";
        }
        if (positionId <= 0) {
            return "岗位 ID 必须为正整数";
        }
        return null;
    }
    
    /**
     * 校验更新投递状态参数
     * @param positionId 岗位 ID
     * @param status 状态值
     * @return 校验结果消息，null 表示校验通过
     */
    public static String validateUpdateDeliveryStatusParams(Integer positionId, Integer status) {
        if (positionId == null) {
            return "岗位 ID 不能为空";
        }
        if (positionId <= 0) {
            return "岗位 ID 必须为正整数";
        }
        if (status == null) {
            return "状态不能为空";
        }
        // 验证状态值是否合法（0-拒绝，2-邀约面试，3-面试通过，4-已入职）
        if (status != 0 && status != 2 && status != 3 && status != 4) {
            return "不支持的状态值";
        }
        return null;
    }
    
    /**
     * 校验用户行为记录参数
     * @param positionId 岗位 ID
     * @return 校验结果消息，null 表示校验通过
     */
    public static String validateUserBehaviorParams(Integer positionId) {
        if (positionId == null) {
            return "岗位 ID 不能为空";
        }
        if (positionId <= 0) {
            return "岗位 ID 必须为正整数";
        }
        return null;
    }
}