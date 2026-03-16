package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;

@Data
public class UserLogin {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String userAccount;
    private String userPassword;
    private Integer userType;

    public static final int USER_TYPE_PERSONAL = 1;
    public static final int USER_TYPE_ENTERPRISE = 2;
    private String saltValue;
    
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;
}