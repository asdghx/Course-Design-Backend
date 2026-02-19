package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;

/**
 * 员工实体类
 * 对应数据库表 job_seeker_info
 */
@Data
@TableName("job_seeker_info")
public class Employee {
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    private String userAccount;
    private String employeeName;
    private String phoneNumber; // 手机号改为String类型以保持格式
    
    private String universityName;
    private String jobIntention;
    private String resume;
    
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;
}