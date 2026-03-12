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
     * 获取文章内容（纯文本）
     */
  public String getArticleContentOnly(Integer id) {
        if (id == null) {
            return null;
        }
        return articleMapper.selectArticleContent(id);
    }

    /**
     * 分页获取文章列表
     */
  public IPage<Article> getArticlePage(String articleType, Integer currentPage, Integer pageSize) {
        Page<Article> page = new Page<>(currentPage, pageSize);
        return articleMapper.selectArticlePage(page, articleType);
    }
    
    /**
     * 增加浏览次数
     */
  public void incrementViewCount(Integer id) {
        if (id != null) {
            articleMapper.incrementViewCount(id);
        }
    }
}
