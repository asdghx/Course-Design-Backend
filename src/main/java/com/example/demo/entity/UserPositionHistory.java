package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.util.Date;

@Data
@TableName("user_position_history")
public class UserPositionHistory {
    private String userAccount;
    private Integer positionId;
    private Integer browseCount = 1;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date lastBrowseTime = new Date();
}