package com.example.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.entity.Article;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

@Repository
public interface ArticleMapper extends BaseMapper<Article> {

    @Select("SELECT id, article_type, title, content, author_account, create_time, view_count FROM article WHERE id = #{id}")
    Article selectArticleBase(@Param("id") Integer id);

    /**
     * 单独查询文章内容（大字段）
     * @param id 文章 ID
     * @return 文章内容
     */
    @Select("SELECT content FROM article WHERE id = #{id}")
    String selectArticleContent(@Param("id") Integer id);

    @Select("SELECT id, article_type, title, create_time, author_account, view_count FROM article WHERE article_type = #{articleType} ORDER BY create_time DESC")
    IPage<Article> selectArticlePage(Page<Article> page, @Param("articleType") String articleType);

    @Update("UPDATE article SET view_count = view_count + 1 WHERE id = #{id}")
    int incrementViewCount(@Param("id") Integer id);

}