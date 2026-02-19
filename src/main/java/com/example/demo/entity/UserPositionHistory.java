package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;

/**
 * 用户岗位浏览历史实体类
 * 对应数据库表 user_position_history
 */
@Data
@TableName("user_position_history")
public class UserPositionHistory {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String userAccount;
    private Integer positionId;
    
    @TableField(fill = FieldFill.INSERT)
    private Date browseTime;
    
    private Integer durationSeconds; // 浏览时长（秒）
}