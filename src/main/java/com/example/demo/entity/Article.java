package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;

/**
 * 文章实体类
 * 对应数据库表 article_info
 */
@Data
@TableName("article_info")
public class Article {
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    private String articleType; // 文章类型：就业指导/创新创业
    private String title;
    private Date publishDate;
    private String content;
    private String authorAccount;
    
    private Integer viewCount; // 浏览次数
    private Integer likeCount; // 点赞次数
    
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;
    
    private Integer status; // 文章状态：1=发布，0=草稿
}