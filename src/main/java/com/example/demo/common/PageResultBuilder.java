package com.example.demo.common;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.ArrayList;
import java.util.List;

/**
 * 分页结果构造器
 * 提供统一的 MyBatis-Plus Page 对象构造和转换功能
 */
public class PageResultBuilder {
    
    /**
     * 基于源分页对象构建新的分页结果
     * 
     * @param sourcePage 源分页对象（包含分页参数和总数信息）
     * @param records 新的记录列表
     * @return 新的分页对象，保持源分页的 current、size、total、pages 信息
     */
    public static <T> Page<T> build(Page<?> sourcePage, List<T> records) {
        Page<T> resultPage = new Page<>(sourcePage.getCurrent(), sourcePage.getSize());
        resultPage.setRecords(records);
        resultPage.setTotal(sourcePage.getTotal());
        resultPage.setPages(sourcePage.getPages());
        return resultPage;
    }
    
    /**
     * 创建空的分页结果
     * 
     * @param current 当前页码
     * @param size 每页大小
     * @return 空分页对象，records 为空 ArrayList，total 为 0
     */
    public static <T> Page<T> buildEmpty(long current, long size) {
        Page<T> emptyPage = new Page<>(current, size);
        emptyPage.setRecords(new ArrayList<>());
        emptyPage.setTotal(0L);
        return emptyPage;
    }
}
