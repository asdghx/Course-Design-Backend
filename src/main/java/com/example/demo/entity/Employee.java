package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;

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