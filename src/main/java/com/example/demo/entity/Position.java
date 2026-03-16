package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;

@Data
@TableName("job")
public class Position {
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    private String employerAccount;
    private String jobDescription;
    private Integer salaryMin;
    private Integer salaryMax;
    private String salaryUnit;
    private String workLocation;
    private Double latitude;
    private Double longitude;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;
    
    private Integer status;
    private String universityName;
    
    @TableField(exist = false)
    private Double distance;
}