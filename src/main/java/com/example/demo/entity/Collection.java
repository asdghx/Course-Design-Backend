package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;

/**
 * 收藏实体类
 * 对应数据库表 collection
 * 使用 (user_account, article_id) 作为复合主键
 */
@Data
@TableName("collection")
public class Collection {
    private String userAccount;  // 用户账号 (复合主键的一部分)
    
    private Integer articleId;   // 文章 ID (复合主键的另一部分)
    
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;
    
    // 移除id字段，使用(userAccount, articleId)作为复合主键
}