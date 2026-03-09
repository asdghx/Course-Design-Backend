package com.example.demo.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * MyBatis-Plus 自动填充配置
 * 处理实体类中带@TableField(fill=...)注解的字段自动填充
 * 
 * 支持的字段:
 * - createTime / create_time: 插入时自动填充创建时间
 * - updateTime / update_time: 更新时自动填充更新时间 (部分表使用 MySQL 的 ON UPDATE，不需要此功能)
 */
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        // 支持驼峰和下划线两种命名风格
        // 创建时间字段自动填充
        this.strictInsertFill(metaObject, "createTime", Date.class, new Date());
        this.strictInsertFill(metaObject, "create_time", Date.class, new Date());
        
        // 更新时间字段自动填充 (如果数据库表有此字段且未使用 MySQL 的 ON UPDATE)
        this.strictInsertFill(metaObject, "updateTime", Date.class, new Date());
        this.strictInsertFill(metaObject, "update_time", Date.class, new Date());
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        // 支持驼峰和下划线两种命名风格
        // 更新时间字段自动填充
        this.strictUpdateFill(metaObject, "updateTime", Date.class, new Date());
        this.strictUpdateFill(metaObject, "update_time", Date.class, new Date());
    }
}