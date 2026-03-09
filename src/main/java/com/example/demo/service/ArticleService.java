package com.example.demo.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.entity.Article;
import com.example.demo.mapper.ArticleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 文章服务类
 * 提供文章相关的业务逻辑
 */
@Service
public class ArticleService {

    private final ArticleMapper articleMapper;
    
    public ArticleService(ArticleMapper articleMapper) {
        this.articleMapper = articleMapper;
    }

    /**
     * 获取文章完整信息（包含内容和基本信息）
     */
    public Article getArticleBase(String id) {
        if (id == null || id.trim().isEmpty()) {
            return null;
        }
        try {
            return articleMapper.selectArticleBase(Integer.parseInt(id));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * 获取文章内容（纯文本）
     */
    public String getArticleContentOnly(String id) {
        if (id == null || id.trim().isEmpty()) {
            return null;
        }
        try {
            return articleMapper.selectArticleContent(Integer.parseInt(id));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * 分页获取文章列表
     */
    public IPage<Article> getArticlePage(String articleType, int currentPage, int pageSize) {
        Page<Article> page = new Page<>(currentPage, pageSize);
        return articleMapper.selectArticlePage(page, articleType);
    }
}