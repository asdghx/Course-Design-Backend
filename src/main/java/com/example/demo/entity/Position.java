package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;

/**
 * 岗位实体类
 * 对应数据库表 job
 */
@Data
@TableName("job")
public class Position {
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    private String employerAccount;
    private String jobDescription;
    private Integer salaryMin; // 最低薪资 (元/月)
    private Integer salaryMax; // 最高薪资 (元/月)
    private String workLocation; // 工作地点
    private Double latitude; // 纬度
    private Double longitude; // 经度
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;
    
    private Integer status; // 岗位状态：1=有效，0=无效
    private String universityName; // 大学名称，为空表示校外岗位
    
    @TableField(exist = false)
    private Double distance; // 与用户位置的距离 (米),仅用于返回结果
}