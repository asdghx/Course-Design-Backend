package com.example.demo.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.common.PositionVOConverter;
import com.example.demo.common.SearchRanker;
import com.example.demo.entity.Article;
import com.example.demo.entity.Position;
import com.example.demo.entity.vo.PositionVO;
import com.example.demo.mapper.SearchMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SearchService {

    private final SearchMapper searchMapper;
    private final PositionVOConverter positionVOConverter;

    public SearchService(SearchMapper searchMapper, PositionVOConverter positionVOConverter) {
        this.searchMapper = searchMapper;
        this.positionVOConverter = positionVOConverter;
    }

    /**
     * 搜索文章
     * @param keyword 搜索关键词
     * @param articleType 文章类型（必填）："就业指导" 或 "创新创业"
     * @return 搜索结果列表
     */
    public List<Article> searchArticles(String keyword, String articleType) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        // 1. 对搜索关键词分词
        List<String> searchKeywords = SearchRanker.tokenize(keyword);
        if (searchKeywords.isEmpty()) {
            return new ArrayList<>();
        }
        
        // 2. 校验文章类型（必填，只有两种：就业指导、创新创业）
        if (articleType == null || articleType.trim().isEmpty()) {
            return new ArrayList<>();  // 未传文章类型，返回空
        }
        if (!"就业指导".equals(articleType) && !"创新创业".equals(articleType)) {
            return new ArrayList<>();  // 无效的文章类型，返回空
        }
        
        // 3. 调用动态 SQL 搜索（粗筛）
        List<Article> articles = searchMapper.searchByKeywords(searchKeywords, articleType);
        
        if (articles.isEmpty()) {
            return articles;
        }
        
        // 4-7. 使用工具类进行精排（提取所有 String 字段、分词、构建矩阵、计算相似度、排序）
        return SearchRanker.rankByRelevance(keyword, articles);
    }

    /**
     * 搜索用户收藏的文章
     * @param userAccount 用户账号
     * @param keyword 搜索关键词
     * @return 搜索结果列表
     */
    public List<Article> searchCollectedArticles(String userAccount, String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        // 1. 对搜索关键词分词
        List<String> searchKeywords = SearchRanker.tokenize(keyword);
        if (searchKeywords.isEmpty()) {
            return new ArrayList<>();
        }
        
        // 2. 调用动态 SQL 搜索（在用户收藏范围内粗筛）
        List<Article> articles = searchMapper.searchCollectedArticles(userAccount, searchKeywords);
        
        if (articles.isEmpty()) {
            return articles;
        }
        
        // 3-6. 使用工具类进行精排（提取所有 String 字段、分词、构建矩阵、计算相似度、排序）
        return SearchRanker.rankByRelevance(keyword, articles);
    }

    /**
     * 搜索校外岗位
     * @param keyword 搜索关键词
     * @return 搜索结果列表（PositionVO）
     */
    public List<PositionVO> searchOutsidePositions(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        System.out.println("\n========== [SearchService] searchOutsidePositions 调用开始 ==========");
        System.out.println("  keyword: " + keyword);
        
        // 1. 对搜索关键词分词
        List<String> searchKeywords = SearchRanker.tokenize(keyword);
        System.out.println("  分词结果：" + searchKeywords);
        
        if (searchKeywords.isEmpty()) {
            System.out.println("  ❌ 分词结果为空，返回空列表");
            System.out.println("========== [SearchService] searchOutsidePositions 调用结束 ==========");
            return new ArrayList<>();
        }
        
        // 2. 调用动态 SQL 搜索（粗筛：只查询校外岗位）
        List<Position> positions = searchMapper.searchOutsidePositions(searchKeywords);
        System.out.println("  SQL 粗筛结果数：" + positions.size());
        
        if (positions.isEmpty()) {
            System.out.println("  ❌ 没有匹配的岗位，返回空列表");
            System.out.println("========== [SearchService] searchOutsidePositions 调用结束 ==========");
            return new ArrayList<>();
        }
        
        // 3. 使用工具类进行精排（提取所有 String 字段、分词、构建矩阵、计算相似度、排序）
        List<Position> rankedPositions = SearchRanker.rankByRelevance(keyword, positions);
        System.out.println("  精排后结果数：" + rankedPositions.size());
        
        // 4. 转换为 PositionVO（包含 companyName）
        List<PositionVO> voList = positionVOConverter.convertToVOList(rankedPositions);
        System.out.println("  转换为 VO 完成");
        System.out.println("========== [SearchService] searchOutsidePositions 调用结束 - 成功返回 ==========");
        
        return voList;
    }
}
