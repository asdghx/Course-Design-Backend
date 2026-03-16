package com.example.demo.controller;

import com.example.demo.common.Result;
import com.example.demo.common.ValidationUtils;
import com.example.demo.entity.Article;
import com.example.demo.entity.vo.PositionVO;
import com.example.demo.service.SearchService;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * 搜索控制器
 */
@RestController
@RequestMapping("/api/search")
@CrossOrigin(origins = "*")
public class SearchController {
    
    private final SearchService searchService;
    
    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }
    
    /**
     * 搜索文章
     */
    @GetMapping("/searchKeyword")
    public List<Article> searchKeyword(@RequestParam String keyword, 
                                       @RequestParam String articleType) {
        // 校验文章类型：只能是"就业指导"或"创新创业"
        if (articleType == null || articleType.trim().isEmpty()) {
            return new ArrayList<>();
        }
        if (!"就业指导".equals(articleType) && !"创新创业".equals(articleType)) {
            return new ArrayList<>();
        }
        
        return searchService.searchArticles(keyword, articleType);
    }

    /**
     * 搜索用户收藏的文章
     */
    @GetMapping("/searchCollectedArticles")
    public Result<List<Article>> searchCollectedArticles(
            HttpServletRequest request,
            @RequestParam String keyword
    ) {
        // ========== Token 验证 ==========
        System.out.println("\n========== [SearchController] searchCollectedArticles 调用开始 ==========");
        
        // 从 Token 解析获取当前登录用户
        String userAccount = (String) request.getAttribute("currentUserAccount");
        System.out.println("【Token 解析结果】");
        System.out.println("  - userAccount: " + userAccount);
        System.out.println("  - keyword: " + keyword);
        
        if (userAccount == null) {
            System.out.println("  ❌ userAccount 为 null（没有 Token 或 Token 无效）");
            System.out.println("========== [SearchController] searchCollectedArticles 调用结束 - 返回 401 ==========");
            return Result.fail("请先登录");
        }
        
        // 参数验证
        String errorMsg = ValidationUtils.validateSearchCollectedParams(userAccount, keyword);
        if (errorMsg != null) {
            System.out.println("  ❌ 参数验证失败：" + errorMsg);
            System.out.println("========== [SearchController] searchCollectedArticles 调用结束 - 返回 400 ==========");
            return Result.badRequest(errorMsg);
        }
        
        System.out.println("  ✅ Token 验证通过，userAccount: " + userAccount);
        System.out.println("  ✅ 参数验证通过");
        
        List<Article> articles = searchService.searchCollectedArticles(userAccount, keyword);
        System.out.println("  ✅ 搜索完成，返回结果数：" + articles.size());
        System.out.println("========== [SearchController] searchCollectedArticles 调用结束 - 成功返回 ==========\n");
        
        return Result.ok(articles);
    }

    /**
     * 搜索校外岗位
     */
    @GetMapping("/searchOutsidePositions")
    public Result<List<PositionVO>> searchOutsidePositions(
            @RequestParam String keyword
    ) {
        System.out.println("\n========== [SearchController] searchOutsidePositions 调用开始 ==========");
        System.out.println("  keyword: " + keyword);
        
        // 参数验证
        if (keyword == null || keyword.trim().isEmpty()) {
            System.out.println("  ❌ 参数验证失败：关键词不能为空");
            System.out.println("========== [SearchController] searchOutsidePositions 调用结束 - 返回 400 ==========");
            return Result.badRequest("搜索关键词不能为空");
        }
        
        System.out.println("  ✅ 参数验证通过");
        
        List<PositionVO> positions = searchService.searchOutsidePositions(keyword);
        System.out.println("  ✅ 搜索完成，返回结果数：" + positions.size());
        System.out.println("========== [SearchController] searchOutsidePositions 调用结束 - 成功返回 ==========\n");
        
        return Result.ok(positions);
    }
}
