package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;

/**
 * 简历投递记录实体类
 * 对应数据库表 resumedelivery
 */
@Data
public class ResumeDelivery {
    @TableId(type = IdType.AUTO)
    private Integer id;  // 投递记录 ID（自增主键）
    
    private String userAccount;
    private Integer positionId;  // 岗位 ID
    
    private Integer deliveryStatus = 1;  // 投递状态，默认已投递
    private Date statusUpdateTime;       // 状态更新时间
    
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;
    
    // 状态常量定义
    public static final int STATUS_REJECTED = 0;   // 已拒绝
    public static final int STATUS_DELIVERED = 1;  // 已投递
    public static final int STATUS_INTERVIEW = 2;  // 邀约面试
    public static final int STATUS_PASSED = 3;     // 面试通过
    public static final int STATUS_HIRED = 4;      // 已入职
}