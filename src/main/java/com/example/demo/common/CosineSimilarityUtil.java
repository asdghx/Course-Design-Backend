package com.example.demo.common;

import java.util.*;

/**
 * 余弦相似度计算工具类
 */
public class CosineSimilarityUtil {

    /**
     * 计算向量间的余弦相似度
     * 
     * @param vectorMatrix 向量矩阵，key 为向量标识，value 为该向量的特征权重 Map
     * @return 相似度矩阵，返回任意两个向量间的余弦相似度
     */
    public static <T> Map<T, Map<T, Double>> calculateCosineSimilarity(
            Map<T, Map<T, Integer>> vectorMatrix) {
        
        // 获取所有向量标识
        Set<T> allVectors = vectorMatrix.keySet();
        List<T> vectorIds = new ArrayList<>(allVectors);
        
        // 计算相似度矩阵
        Map<T, Map<T, Double>> similarityMatrix = new HashMap<>();
        
        for (int i = 0; i < vectorIds.size(); i++) {
            for (int j = i; j < vectorIds.size(); j++) {
                T vectorA = vectorIds.get(i);
                T vectorB = vectorIds.get(j);
                
                // 计算向量点积、模长
                double dotProduct = 0.0;  // Σ(Ai * Bi)
                double normA = 0.0;       // √ΣAi²
                double normB = 0.0;       // √ΣBi²
                
                // 遍历所有特征，计算点积和模长
                for (T feature : allVectors) {
                    int scoreA = vectorMatrix.getOrDefault(vectorA, new HashMap<>())
                            .getOrDefault(feature, 0);
                    int scoreB = vectorMatrix.getOrDefault(vectorB, new HashMap<>())
                            .getOrDefault(feature, 0);
                    
                    dotProduct += scoreA * scoreB;
                    normA += scoreA * scoreA;
                    normB += scoreB * scoreB;
                }
                
                // 计算余弦相似度
                double cosineSimilarity = 0.0;
                if (normA > 0 && normB > 0) {
                    cosineSimilarity = dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
                }
                
                // 存储相似度矩阵
                similarityMatrix.computeIfAbsent(vectorA, k -> new HashMap<>())
                        .put(vectorB, cosineSimilarity);
                if (!vectorA.equals(vectorB)) {
                    similarityMatrix.computeIfAbsent(vectorB, k -> new HashMap<>())
                            .put(vectorA, cosineSimilarity);
                }
            }
        }
        
        return similarityMatrix;
    }
}
