package com.example.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.entity.Article;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface SearchMapper extends BaseMapper<Article> {

    /**
     * 根据关键词搜索文章（动态 SQL）
     * @param keywords 分词后的关键词列表
     * @param articleType 文章类型（可选）："就业指导" 或 "创新创业"
     * @return 匹配的文章列表
     */
    List<Article> searchByKeywords(@Param("keywords") List<String> keywords,
                                   @Param("articleType") String articleType);
}
