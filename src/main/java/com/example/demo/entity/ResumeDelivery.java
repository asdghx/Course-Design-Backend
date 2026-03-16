package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;

@Data
public class ResumeDelivery {
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    private String userAccount;
    private Integer positionId;
    
    private Integer deliveryStatus = 1;
    private Date statusUpdateTime;
    
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;
    
    private Integer hasUpdate = 1;  // 更新标识：0-已读，1-企业有新操作
    
    public static final int STATUS_REJECTED = 0;
    public static final int STATUS_DELIVERED = 1;
    public static final int STATUS_INTERVIEW = 2;
    public static final int STATUS_PASSED = 3;
    public static final int STATUS_HIRED = 4;
}