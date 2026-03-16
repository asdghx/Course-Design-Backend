package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;

@Data
public class Collection {
    private String userAccount;
    
    private Integer articleId;
    
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;
}