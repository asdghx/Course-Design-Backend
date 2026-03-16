package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;

@Data
public class Article {
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    private String articleType;
    private String title;
    private String content;
    private String authorAccount;
    
    private Integer viewCount = 0;
    
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;
}