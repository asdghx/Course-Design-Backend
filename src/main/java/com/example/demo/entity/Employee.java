package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;

/**
 * 员工实体类
 * 对应数据库表 employee
 * userAccount 为主键，关联 user_login 表
 */
@Data
public class Employee {
    @TableId(type = IdType.NONE)
    private String userAccount;
    private String employeeName;
    private String phoneNumber;
    private String universityName;
    private String jobIntention;
    private String resume;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;
}