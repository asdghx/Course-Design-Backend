package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;

/**
 * 收藏实体类
 * 对应数据库表 article_collection
 */
@Data
@TableName("article_collection")
public class Collection {
    private String userAccount;  // 用户账号
    private Integer articleId;   // 文章ID
    
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;
    
    // 移除id字段，使用(userAccount, articleId)作为复合主键
}