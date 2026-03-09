package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;

/**
 * 文章实体类
 * 对应数据库表 article
 */
@Data
public class Article {
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    private String articleType; // 文章类型：就业指导/创新创业
    private String title;
    // publishDate 字段已移除，使用 createTime 作为发布时间
    private String content;
    private String authorAccount;
    
    private Integer viewCount = 0; // 浏览次数，默认值 0
    
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;
    
    // 移除likeCount和updateTime字段，与SQL结构保持一致
}