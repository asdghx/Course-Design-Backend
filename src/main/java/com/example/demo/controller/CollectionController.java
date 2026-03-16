package com.example.demo.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.common.Result;
import com.example.demo.common.ValidationUtils;
import com.example.demo.entity.Article;
import com.example.demo.entity.Collection;
import com.example.demo.service.CollectionService;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * 收藏控制器
 */
@RestController
@RequestMapping("/api/collection")
@CrossOrigin("*")
public class CollectionController {
    private final CollectionService collectionService;

    public CollectionController(CollectionService collectionService) {
        this.collectionService = collectionService;
    }

    /**
     * 创建收藏
     */
    @PostMapping("/createCollection")
    public Result<String> createCollection(
            HttpServletRequest request,
            @RequestParam("articleId") Integer articleId
    ){
        // 从 Token 解析获取当前登录用户
        String userAccount = (String) request.getAttribute("currentUserAccount");
        if (userAccount == null) {
            return Result.fail("请先登录");
        }
        
        // 参数验证
        String errorMsg = ValidationUtils.validateCollectionParams(articleId);
        if (errorMsg != null) {
            return Result.badRequest(errorMsg);
        }
        
        String msg = collectionService.createCollection(articleId, userAccount);
        return msg.equals("收藏成功") ? Result.ok(msg) : Result.fail(msg);
    }

    /**
     * 删除收藏
     */
    @PostMapping("/deleteCollection")
    public Result<String> deleteCollection(
            HttpServletRequest request,
            @RequestParam("articleId") Integer articleId
    ){
        // 从 Token 解析获取当前登录用户
        String userAccount = (String) request.getAttribute("currentUserAccount");
        if (userAccount == null) {
            return Result.fail("请先登录");
        }
        
        // 参数验证
        String errorMsg = ValidationUtils.validateCollectionParams(articleId);
        if (errorMsg != null) {
            return Result.badRequest(errorMsg);
        }
        
        String msg = collectionService.deleteCollection(articleId, userAccount);
        return msg.equals("取消收藏成功") ? Result.ok(msg) : Result.fail(msg);
    }

    /**
     * 查询收藏状态
     */
    @GetMapping("/getCollection")
    public Result<Collection> getCollection(
            HttpServletRequest request,
            @RequestParam("articleId") Integer articleId
    ){
        // 从 Token 解析获取当前登录用户
        String userAccount = (String) request.getAttribute("currentUserAccount");
        if (userAccount == null) {
            return Result.fail("请先登录");
        }
        
        // 参数验证
        String errorMsg = ValidationUtils.validateCollectionParams(articleId);
        if (errorMsg != null) {
            return Result.badRequest(errorMsg);
        }
        
        Collection collection = collectionService.getCollectionByUserAccountAndArticleId(userAccount, articleId);
        return Result.ok(collection);
    }
    
    /**
     * 分页获取用户收藏的文章
     */
    @GetMapping("/getCollectedArticles")
    public Result<IPage<Article>> getCollectedArticlesByUserAccountWithPagination(
            HttpServletRequest request,
            @RequestParam(value = "currentPage", defaultValue = "1") Integer currentPage,
            @RequestParam(value = "pageSize", defaultValue = "8") Integer pageSize
    ) {
        // ========== 打印完整调用链 ==========
        System.out.println("\n========== [CollectionController] getCollectedArticles 调用开始 ==========");
        
        // 1. 打印请求信息
        System.out.println("【1. 请求信息】");
        System.out.println("  - 请求 URL: " + request.getRequestURI());
        System.out.println("  - 请求方法: " + request.getMethod());
        System.out.println("  - currentPage: " + currentPage);
        System.out.println("  - pageSize: " + pageSize);
        
        // 2. 打印 Token相关信息
        System.out.println("\n【2. Token 信息】");
        String token = request.getHeader("Authorization");
        if (token == null || token.trim().isEmpty()) {
            System.out.println("  ❌ Header 中没有 Authorization");
        } else {
            System.out.println("  ✅ Authorization: " + token.substring(0, Math.min(50, token.length())) + "...");
        }
        
        // 3. 从 Token 解析获取当前登录用户
        System.out.println("\n【3. 从 request 获取用户信息】");
        String userAccount = (String) request.getAttribute("currentUserAccount");
        Integer userType = (Integer) request.getAttribute("currentUserType");
        Long userId = (Long) request.getAttribute("currentUserId");
        
        if (userAccount == null) {
            System.out.println("  ❌ currentUserAccount: null（拦截器未设置，可能是没有 Token）");
            System.out.println("\n========== [CollectionController] getCollectedArticles 调用结束 - 返回 401 ==========");
            return Result.fail("请先登录");
        } else {
            System.out.println("  ✅ currentUserAccount: " + userAccount);
            System.out.println("  ✅ currentUserType: " + userType);
            System.out.println("  ✅ currentUserId: " + userId);
        }
        
        // 4. 调用 Service
        System.out.println("\n【4. 调用 Service】");
        System.out.println("  - 调用 collectionService.getCollectedArticlesByUserAccountWithPagination()");
        System.out.println("  - 参数：userAccount=" + userAccount + ", currentPage=" + currentPage + ", pageSize=" + pageSize);
        
        IPage<Article> pageResult = collectionService.getCollectedArticlesByUserAccountWithPagination(userAccount, currentPage, pageSize);
        
        // 5. 打印返回结果
        System.out.println("\n【5. Service 返回结果】");
        System.out.println("  - 总记录数：" + pageResult.getTotal());
        System.out.println("  - 总页数：" + pageResult.getPages());
        System.out.println("  - 当前页：" + pageResult.getCurrent());
        System.out.println("  - 每页大小：" + pageResult.getSize());
        System.out.println("  - 返回记录数：" + pageResult.getRecords().size());
        
        System.out.println("\n========== [CollectionController] getCollectedArticles 调用结束 - 成功返回 ==========");
        return Result.ok(pageResult);
    }
}