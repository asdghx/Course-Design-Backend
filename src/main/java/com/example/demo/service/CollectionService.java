package com.example.demo.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.entity.Article;
import com.example.demo.entity.Collection;
import com.example.demo.mapper.CollectionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 收藏服务类
 * 提供文章收藏相关的业务逻辑
 */
@Service
public class CollectionService {
    private final CollectionMapper collectionMapper;
    
    public CollectionService(CollectionMapper collectionMapper) {
        this.collectionMapper = collectionMapper;
    }

    /**
     * 根据用户账号和文章 ID 查询收藏状态
     */
    public Collection getCollectionByUserAccountAndArticleId(String userAccount, Integer articleId){
        return collectionMapper.selectByUserAndArticle(userAccount, articleId);
    }
    
    /**
     * 分页查询用户收藏的文章列表
     */
    public IPage<Article> getCollectedArticlesByUserAccountWithPagination(String userAccount, Integer currentPage, Integer pageSize){
        Page<Article> page = new Page<>(currentPage, pageSize);
        return collectionMapper.selectCollectedArticles(page, userAccount);
    }

    /**
     * 创建收藏
     */
    public String createCollection(Integer articleId, String userAccount){
        // 检查是否已收藏
        Collection existing = collectionMapper.selectByUserAndArticle(userAccount, articleId);
        if (existing != null) {
            return "已收藏该文章";
        }
            
        Collection collection = new Collection();
        collection.setUserAccount(userAccount);
        collection.setArticleId(articleId);
        int rows = collectionMapper.insertCollection(collection);
        return rows > 0 ? "收藏成功" : "收藏失败";
    }
        
    /**
     * 删除收藏
     */
    public String deleteCollection(Integer articleId, String userAccount){
        int rows = collectionMapper.deleteCollection(articleId, userAccount);
        return rows > 0 ? "取消收藏成功" : "取消收藏失败";
    }
    

}