package com.example.demo.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.demo.common.Result;
import com.example.demo.entity.Article;
import com.example.demo.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 文章相关API控制器
 * 提供文章列表和详情查询功能
 */
@RestController
@RequestMapping("/api/article")
@CrossOrigin("*")
public class ArticleController {
    @Autowired
    ArticleService articleService;

    /**
     * 获取文章分页列表
     */
    @GetMapping("/getArticlePage")
    public Result<IPage<Article>> getArticlePage(
            @RequestParam("articleType") String articleType,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "8") int size
    ) {
        IPage<Article> articlePage = articleService.getArticlePage(articleType, page, size);
        return Result.ok(articlePage);
    }

    /**
     * 获取文章完整信息（包含内容和基本信息）
     */
    @GetMapping("/getArticleBase")
    public Result<Article> getArticleBase(
            @RequestParam("id") String id
    ){
        Article article = articleService.getArticleBase(id);
        return Result.ok(article);
    }

    /**
     * 获取文章内容（纯文本）
     */
    @GetMapping("/getArticleContent")
    public Result<String> getArticleContent(
            @RequestParam("id") String id
    ){
        String content = articleService.getArticleContentOnly(id);
        return Result.ok(content);
    }
}