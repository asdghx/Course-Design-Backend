package com.example.demo.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.demo.common.Result;
import org.springframework.web.bind.annotation.*;

/**
 * 申请相关API控制器
 * 提供申请列表查询等功能
 */
@RestController
@RequestMapping("/api/application")
@CrossOrigin("*")
public class ApplicationController {

    /**
     * 查询申请列表接口
     * TODO: 需要实现具体的业务逻辑
     */
    @GetMapping("/getApplicationPage")
    public Result<IPage<Object>> getApplicationPage(
            @RequestParam(value = "current", defaultValue = "1") Integer current,
            @RequestParam(value = "size", defaultValue = "10") Integer size,
            @RequestParam(value = "employerAccount", required = false) String employerAccount) {
        
        // TODO: 这里需要实现具体的申请列表查询逻辑
        // 暂时返回空的分页结果作为占位
        IPage<Object> emptyPage = new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>();
        emptyPage.setCurrent(current);
        emptyPage.setSize(size);
        emptyPage.setRecords(new java.util.ArrayList<>());
        emptyPage.setTotal(0L);
        emptyPage.setPages(0L);
        
        return Result.ok(emptyPage);
    }
}