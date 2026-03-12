package com.example.demo.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.demo.common.Result;
import com.example.demo.entity.Article;
import com.example.demo.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 文章相关 API 控制器
 * 提供文章列表和详情查询功能
 */
@RestController
@RequestMapping("/api/article")
@CrossOrigin("*")
public class ArticleController {
    @Autowired
    ArticleService articleService;

    /**
     * 获取文章分页列表（仅基本信息，不包含内容）
     */
    @GetMapping("/getArticlePage")
   public Result<IPage<Article>> getArticlePage(
            @RequestParam("articleType") String articleType,
            @RequestParam(value = "currentPage", defaultValue = "1") Integer currentPage,
            @RequestParam(value = "pageSize", defaultValue = "8") Integer pageSize
    ) {
       IPage<Article> articlePage = articleService.getArticlePage(articleType, currentPage, pageSize);
        
        // 打印调试信息
        System.out.println("【分页查询】文章类型：" + articleType + ", 当前页：" + currentPage + 
                          ", 每页大小：" + pageSize + ", 总记录数：" + articlePage.getTotal() + 
                          ", 总页数：" + articlePage.getPages() + ", 返回记录数：" + articlePage.getRecords().size());
        
        return Result.ok(articlePage);
    }

    /**
     * 获取文章内容（纯文本）并增加浏览次数
     */
    @GetMapping("/getArticleContent")
   public Result<String> getArticleContent(
            @RequestParam("id") Integer id
    ){
        if (id == null) {
            return Result.badRequest("文章 ID 不能为空");
        }
        String content = articleService.getArticleContentOnly(id);
        if (content == null) {
            return Result.fail("文章不存在");
        }
        // 增加浏览次数
        articleService.incrementViewCount(id);
        return Result.ok(content);
    }
}
