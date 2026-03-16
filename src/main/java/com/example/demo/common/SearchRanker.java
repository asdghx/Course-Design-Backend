package com.example.demo.common;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.seg.common.Term;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 搜索排序工具类
 * 基于余弦相似度和 TF-IDF 的通用搜索排序工具
 * 
 * 使用方式：
 * 1. Controller 接收搜索关键词
 * 2. Service 调用 SQL 获取粗筛结果
 * 3. 调用 rankByRelevance(keyword, resultList) 进行精排
 * 4. 返回排序后的结果
 */
public class SearchRanker {

    /**
     * 根据相关性对搜索结果进行排序
     * 
     * @param keyword 搜索关键词
     * @param dataList SQL 查询返回的原始数据列表
     * @return 按相似度降序排序后的列表
     */
    public static <T> List<T> rankByRelevance(String keyword, List<T> dataList) {
        if (keyword == null || keyword.trim().isEmpty() || dataList == null || dataList.isEmpty()) {
            return dataList != null ? dataList : new ArrayList<>();
        }

        // 1. 对搜索关键词分词并去重
        List<String> searchKeywords = tokenize(keyword).stream().distinct().collect(Collectors.toList());
        if (searchKeywords.isEmpty()) {
            return new ArrayList<>();
        }

        // 2. 为每个对象计算覆盖率和词频
        List<ScoredObject<T>> scoredList = new ArrayList<>();
        
        for (T obj : dataList) {
            String fullText = extractAllStringFields(obj);
            List<String> objectWords = tokenize(fullText);
            
            // 对文章分词去重（关键优化！）
            Set<String> uniqueWords = new HashSet<>(objectWords);
            
            // 计算匹配的词数量（覆盖率）
            int matchedCount = 0;
            int totalFreq = 0;
            
            for (String searchWord : searchKeywords) {
                String normalizedWord = normalizeWord(searchWord);
                if (uniqueWords.contains(normalizedWord)) {
                    matchedCount++;  // 覆盖了一个词
                }
                // 统计该词在文章中出现的总次数（用于第二排序）
                long freq = objectWords.stream()
                        .filter(w -> normalizeWord(w).equals(normalizedWord))
                        .count();
                totalFreq += freq;
            }
            
            // 计算覆盖率
            double coverageRate = (double) matchedCount / searchKeywords.size();
            
            scoredList.add(new ScoredObject<>(obj, coverageRate, totalFreq));
        }
        
        // 3. 排序：先按覆盖率，再按词频
        scoredList.sort((a, b) -> {
            // 第一优先级：覆盖率（越高越好）
            if (Double.compare(b.coverageRate, a.coverageRate) != 0) {
                return Double.compare(b.coverageRate, a.coverageRate);
            }
            // 第二优先级：总词频（越高越好）
            return Integer.compare(b.totalFreq, a.totalFreq);
        });
        
        // 4. 提取排序后的对象
        return scoredList.stream()
                .map(so -> so.object)
                .collect(Collectors.toList());
    }

    /**
     * 提取对象的所有 String 类型字段并拼接
     * 
     * @param obj 要提取字段的对象
     * @return 所有 String 字段的拼接结果（用空格分隔）
     */
    private static String extractAllStringFields(Object obj) {
        if (obj == null) {
            return "";
        }

        StringBuilder fullText = new StringBuilder();
        Class<?> clazz = obj.getClass();

        // 遍历当前类的所有字段
        for (Field field : clazz.getDeclaredFields()) {
            if (field.getType() == String.class) {
                field.setAccessible(true);
                try {
                    Object value = field.get(obj);
                    if (value != null) {
                        fullText.append(" ").append(value);
                    }
                } catch (IllegalAccessException e) {
                    // 忽略无法访问的字段
                }
            }
        }

        // 遍历父类的字段（如果有）
        Class<?> superClass = clazz.getSuperclass();
        while (superClass != null && superClass != Object.class) {
            for (Field field : superClass.getDeclaredFields()) {
                if (field.getType() == String.class) {
                    field.setAccessible(true);
                    try {
                        Object value = field.get(obj);
                        if (value != null) {
                            fullText.append(" ").append(value);
                        }
                    } catch (IllegalAccessException e) {
                        // 忽略无法访问的字段
                    }
                }
            }
            superClass = superClass.getSuperclass();
        }

        return fullText.toString().trim();
    }

    /**
     * 中文分词工具（使用 HanLP）
     * 
     * @param text 待分词的文本
     * @return 分词后的列表（过滤掉单字词）
     */
    public static List<String> tokenize(String text) {
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
     * 标准化词语（仅对英文单词转小写）
     * 
     * @param word 待标准化的词语
     * @return 标准化后的词语
     */
    private static String normalizeWord(String word) {
        return word.matches("[a-zA-Z]+") ? word.toLowerCase() : word;
    }
    
    /**
     * 内部类：带评分的对象
     */
    private static class ScoredObject<T> {
        T object;           // 原始对象
        double coverageRate; // 覆盖率（0-1）
        int totalFreq;      // 总词频
        
        ScoredObject(T object, double coverageRate, int totalFreq) {
            this.object = object;
            this.coverageRate = coverageRate;
            this.totalFreq = totalFreq;
        }
    }
}
