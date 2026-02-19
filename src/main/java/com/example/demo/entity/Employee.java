package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;

/**
 * 员工实体类
 * 对应数据库表 job_seeker_info
 * userAccount 为主键，关联 user_login 表
 */
@Data
@TableName("job_seeker_info")
public class Employee {
    @TableId(type = IdType.NONE)
    private String userAccount;  // 主键，关联user_login表
    private String employeeName;
    private String phoneNumber; // 手机号改为String类型以保持格式
    
    private String universityName;
    private String jobIntention;
    private String resume;
    
    // 移除createTime字段，只保留updateTime
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;
}