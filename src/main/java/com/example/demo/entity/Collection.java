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
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String userAccount;
    private Integer articleId;
    
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;
}