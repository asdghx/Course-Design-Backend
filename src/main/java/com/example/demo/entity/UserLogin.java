package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;

/**
 * 用户登录实体类
 * 对应数据库表 user_login
 */
@Data
@TableName("user_login")
public class UserLogin {
    @TableId(type = IdType.AUTO)
    private Long id; // 自增主键
    
    private String userAccount; // 用户账号（手机号/企业编号）
    private String userPassword; // 用户密码（MD5加密后）
    private Integer userType; // 用户类型
    
    // 用户类型常量定义
    public static final int USER_TYPE_PERSONAL = 1;  // 个人用户
    public static final int USER_TYPE_ENTERPRISE = 2; // 企业用户
    private String saltValue; // 盐值
    
    @TableField(fill = FieldFill.INSERT)
    private Date createTime; // 创建时间
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime; // 更新时间
    
    private Integer status; // 账户状态：1=正常，0=禁用
}