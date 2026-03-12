package com.example.demo.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.common.Result;
import com.example.demo.entity.Article;
import com.example.demo.entity.Collection;
import com.example.demo.service.CollectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 收藏相关 API 控制器
 * 提供文章收藏、取消收藏、查询等功能
 */
@RestController
@RequestMapping("/api/collection")
@CrossOrigin("*")
public class CollectionController {
    @Autowired
    CollectionService collectionService;

    /**
     * 创建收藏接口
     */
    @PostMapping("/createCollection")
    public Result<String> createCollection(
            @RequestParam("userAccount") String userAccount,
            @RequestParam("articleId") Integer articleId
    ){
        String msg = collectionService.createCollection(articleId, userAccount);
        return msg.equals("收藏成功") ? Result.ok(msg) : Result.fail(msg);
    }

    /**
     * 删除收藏接口
     */
    @PostMapping("/deleteCollection")
    public Result<String> deleteCollection(
            @RequestParam("userAccount") String userAccount,
            @RequestParam("articleId") Integer articleId
    ){
        String msg = collectionService.deleteCollection(articleId, userAccount);
        return msg.equals("取消收藏成功") ? Result.ok(msg) : Result.fail(msg);
    }

    /**
     * 查询收藏状态接口
     */
    @GetMapping("/getCollection")
    public Result<Collection> getCollection(
            @RequestParam("userAccount") String userAccount,
            @RequestParam("articleId") Integer articleId
    ){
        Collection collection = collectionService.getCollectionByUserAccountAndArticleId(userAccount, articleId);
        return Result.ok(collection);
    }
    
    /**
     * 分页查询用户收藏的文章列表
     */
    @GetMapping("/getCollectedArticles")
    public Result<IPage<Article>> getCollectedArticlesByUserAccountWithPagination(
            @RequestParam("userAccount") String userAccount,
            @RequestParam(value = "currentPage", defaultValue = "1") Integer currentPage,
            @RequestParam(value = "pageSize", defaultValue = "8") Integer pageSize
    ) {
        IPage<Article> pageResult = collectionService.getCollectedArticlesByUserAccountWithPagination(userAccount, currentPage, pageSize);
        return Result.ok(pageResult);
    }
}