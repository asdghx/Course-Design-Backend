package com.example.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.entity.Article;
import com.example.demo.entity.Collection;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

/**
 * 文章收藏Mapper接口
 * 继承MyBatis-Plus的BaseMapper，提供基础CRUD功能
 * 自定义扩展方法用于收藏相关的业务操作
 */
@Repository
public interface CollectionMapper extends BaseMapper<Collection> {

    /**
     * 根据用户账号和文章ID查询收藏记录
     * @param userAccount 用户账号
     * @param articleId 文章ID
     * @return 收藏记录
     */
    Collection selectByUserAndArticle(@Param("userAccount") String userAccount, @Param("articleId") Integer articleId);

    /**
     * 分页查询用户收藏的文章
     * @param page 分页对象
     * @param userAccount 用户账号
     * @return 收藏文章分页结果
     */
    IPage<Article> selectCollectedArticles(Page<Article> page, @Param("userAccount") String userAccount);

    /**
     * 添加文章收藏
     * @param collection 收藏对象
     * @return 影响行数
     */
    int insertCollection(Collection collection);

    /**
     * 删除文章收藏
     * @param articleId 文章ID
     * @param userAccount 用户账号
     * @return 影响行数
     */
    int deleteCollection(@Param("articleId") Integer articleId, @Param("userAccount") String userAccount);
}