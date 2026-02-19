package com.example.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.entity.Article;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

@Repository
public interface ArticleMapper extends BaseMapper<Article> {

    @Select("SELECT id, title, author_account, publish_date, content, view_count, like_count FROM article_info WHERE id = #{id} AND status = 1")
    Article selectArticleContent(@Param("id") Integer id);

    @Select("SELECT id, title, publish_date, author_account, view_count, like_count FROM article_info WHERE article_type = #{articleType} AND status = 1 ORDER BY publish_date DESC")
    IPage<Article> selectArticlePage(Page<Article> page, @Param("articleType") String articleType);

    @Update("UPDATE article_info SET view_count = view_count + 1 WHERE id = #{id}")
    int incrementViewCount(@Param("id") Integer id);

    @Update("UPDATE article_info SET like_count = like_count + 1 WHERE id = #{id}")
    int incrementLikeCount(@Param("id") Integer id);

    @Select("SELECT COUNT(*) FROM article_info WHERE author_account = #{authorAccount} AND status = 1")
    int countPublishedArticlesByAuthor(@Param("authorAccount") String authorAccount);
}