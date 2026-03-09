package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;

/**
 * 雇主/企业实体类
 * 对应数据库表 employer
 * employerAccount 为主键，关联 user_login 表
 */
@Data
public class Employer {
    @TableId(type = IdType.NONE)
    private String employerAccount;
    private String companyName;
    private String contactPhone;
    private String contactEmail;
    private String companyProfile;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;
}
