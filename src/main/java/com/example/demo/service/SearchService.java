package com.example.demo.service;

import com.example.demo.common.CosineSimilarityUtil;
import com.example.demo.entity.Article;
import com.example.demo.mapper.SearchMapper;
import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.seg.common.Term;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class SearchService {

    @Autowired
    private SearchMapper searchMapper;

    /**
     * 中文分词工具（使用 HanLP）
     */
    public List<String> tokenize(String text) {
        if (text == null || text.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        // 使用 HanLP 进行智能分词
        List<Term> terms = HanLP.segment(text);
        
        // 过滤掉无意义的词（停用词、标点符号等）
        return terms.stream()
                .map(term -> term.word.trim())
                .filter(word -> !word.isEmpty())
                .filter(word -> word.length() >= 2)  // 只保留 2 个字以上的词
                .collect(Collectors.toList());
    }

    /**
     * 搜索文章
     * @param keyword 搜索关键词
     * @param articleType 文章类型（可选）："就业指导" 或 "创新创业"
     * @return 搜索结果列表
     */
    public List<Article> searchArticles(String keyword, String articleType) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        // 1. 对搜索关键词分词
        List<String> searchKeywords = tokenize(keyword);
        if (searchKeywords.isEmpty()) {
            return new ArrayList<>();
        }
        
        // 2. 校验文章类型（只有两种：就业指导、创新创业）
        if (articleType != null && !articleType.trim().isEmpty()) {
            if (!"就业指导".equals(articleType) && !"创新创业".equals(articleType)) {
                articleType = null;  // 无效类型则不限制
            }
        }
        
        // 3. 调用动态 SQL 搜索（粗筛）
        List<Article> articles = searchMapper.searchByKeywords(searchKeywords, articleType);
        
        if (articles.isEmpty()) {
            return articles;
        }
        
        // 4. 对每个文章进行分词，并构建词频向量
        Map<Integer, List<String>> articleTokensMap = new HashMap<>();  // 文章 ID -> 分词列表
        Map<String, Map<String, Integer>> termFreqMatrix = new HashMap<>();  // 向量名 -> 词频 Map
        
        // 初始化搜索词向量
        Map<String, Integer> searchWordFreq = new HashMap<>();
        for (String kw : searchKeywords) {  // 改名 keyword -> kw，避免冲突
            searchWordFreq.merge(normalizeWord(kw), 1, Integer::sum);
        }
        termFreqMatrix.put("search_query", searchWordFreq);
        
        // 为每篇文章构建词频向量
        for (Article article : articles) {
            String fullText = Objects.toString(article.getTitle(), "") + " " +
                            Objects.toString(article.getContent(), "");
            List<String> articleWords = tokenize(fullText);
            articleTokensMap.put(article.getId(), articleWords);
            
            Map<String, Integer> wordFreq = new HashMap<>();
            for (String word : articleWords) {
                wordFreq.merge(normalizeWord(word), 1, Integer::sum);
            }
            
            termFreqMatrix.put("article_" + article.getId(), wordFreq);
        }
        
        // 5. 使用词频矩阵计算相似度
        // 6. 使用余弦相似度计算搜索词与各文章的相似度
        Map<Integer, Double> articleSimilarityScores = calculateSimilarityScores(termFreqMatrix, searchKeywords);
        
        // 7. 按相似度分数降序排序
        List<Article> sortedArticles = articles.stream()
                .sorted((a1, a2) -> Double.compare(
                        articleSimilarityScores.getOrDefault(a2.getId(), 0.0),
                        articleSimilarityScores.getOrDefault(a1.getId(), 0.0)))
                .collect(Collectors.toList());
        
        return sortedArticles;
    }



    private Map<Integer, Double> calculateSimilarityScores(
            Map<String, Map<String, Integer>> termFreqMatrix,
            List<String> searchKeywords) {
        
        Map<Integer, Double> scores = new HashMap<>();
        
        // 调用通用余弦相似度工具
        Map<String, Map<String, Double>> similarityMatrix = 
                CosineSimilarityUtil.calculateCosineSimilarity(termFreqMatrix);
        
        // 提取搜索词与各文章的相似度
        String searchVectorKey = "search_query";
        Map<String, Double> searchSimilarities = similarityMatrix.get(searchVectorKey);
        
        if (searchSimilarities != null) {
            for (Map.Entry<String, Double> entry : searchSimilarities.entrySet()) {
                String key = entry.getKey();
                if (key.startsWith("article_")) {
                    Integer articleId = Integer.parseInt(key.substring(8));
                    scores.put(articleId, entry.getValue());
                }
            }
        }
        
        return scores;
    }
    
    /**
     * 标准化词语（仅对英文单词转小写）
     */
    private String normalizeWord(String word) {
        return word.matches("[a-zA-Z]+") ? word.toLowerCase() : word;
    }
}
