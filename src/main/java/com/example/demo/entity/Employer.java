package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;

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
