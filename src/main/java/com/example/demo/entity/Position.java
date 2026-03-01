package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;

/**
 * 岗位实体类
 * 对应数据库表 job_info
 */
@Data
@TableName("job_info")
public class Position {
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    private String employerAccount;
    private String jobDescription;
    private String salaryRange;
    private String workLocation; // 工作地点
    private String experienceRequirement; // 经验要求
    private String educationRequirement; // 学历要求
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;
    
    private Integer status; // 岗位状态：1=有效，0=无效
    private String universityName; // 大学名称，为空表示校外岗位
}