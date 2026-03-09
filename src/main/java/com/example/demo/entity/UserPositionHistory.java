package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.util.Date;

@Data
public class UserPositionHistory {
    private String userAccount;
    private Integer positionId;
    private Integer browseCount;
    private Integer deliveryCount;
    private Integer passCount;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;
}