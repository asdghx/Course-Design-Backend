package com.example.demo.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.entity.Article;
import com.example.demo.mapper.ArticleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * 文章服务类
 * 提供文章相关的业务逻辑
 */
@Service
public class ArticleService {

    @Autowired
    private ArticleMapper articleMapper;

    /**
     * 获取文章详情
     */
    @Cacheable(value = "article", key = "#id")
    public Article getArticleContent(String id) {
        Article article = articleMapper.selectArticleContent(Integer.parseInt(id));
        if (article != null) {
            // 增加浏览次数
            articleMapper.incrementViewCount(Integer.parseInt(id));
        }
        return article;
    }

    /**
     * 分页获取文章列表
     */
    @Cacheable(value = "articles", key = "#articleType + '_' + #currentPage + '_' + #pageSize")
    public IPage<Article> getArticlePage(String articleType, int currentPage, int pageSize) {
        Page<Article> page = new Page<>(currentPage, pageSize);
        return articleMapper.selectArticlePage(page, articleType);
    }
}