package com.example.demo.controller;

import com.example.demo.entity.Article;
import com.example.demo.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/search")
@CrossOrigin(origins = "*")  // 允许跨域
public class SearchController {
    
    @Autowired
    private SearchService searchService;
    
    /**
     * 搜索接口
     * @param keyword 搜索关键词
     * @param articleType 文章类型（可选）："就业指导" 或 "创新创业"
     * @return 搜索结果
     */
    @GetMapping("/searchKeyword")
    public List<Article> searchKeyword(@RequestParam String keyword, 
                                       @RequestParam(required = false) String articleType) {
        // 校验文章类型：只能是"就业指导"或"创新创业"
        if (articleType != null && !articleType.trim().isEmpty()) {
            if (!"就业指导".equals(articleType) && !"创新创业".equals(articleType)) {
                return new ArrayList<>();  // 或者抛出异常
            }
        }
        
        return searchService.searchArticles(keyword, articleType);
    }
}
