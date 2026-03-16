package com.example.demo.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.common.Result;
import com.example.demo.common.ValidationUtils;
import com.example.demo.entity.vo.PositionVO;
import com.example.demo.service.PositionManagementService;
import com.example.demo.service.PositionRecommendationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 岗位控制器
 */
@RestController
@RequestMapping("/api/position")
@CrossOrigin("*")
public class PositionController {

    private final PositionManagementService positionManagementService;
    private final PositionRecommendationService positionRecommendationService;
    public PositionController(PositionManagementService positionManagementService,
                              PositionRecommendationService positionRecommendationService) {
        this.positionManagementService = positionManagementService;
        this.positionRecommendationService = positionRecommendationService;
    }

    // ========== 岗位推荐接口 ==========

    /**
     * 个性化岗位推荐
     */
    @GetMapping("/getPositionRecommendations")
    public Result<Page<PositionVO>> getPositionRecommendations(
            HttpServletRequest request,
            @RequestParam(value = "currentPage", defaultValue = "1") Integer currentPage,
            @RequestParam(value = "pageSize", defaultValue = "8") Integer pageSize,
            @RequestParam(value = "universityName", required = false) String universityName){
        
        System.out.println("\n========== [PositionController] getPositionRecommendations 调用开始 ==========");
        
        // 从 Token 解析获取当前登录用户
        String userAccount = (String) request.getAttribute("currentUserAccount");
        System.out.println("【Token 解析结果】");
        System.out.println("  - userAccount: " + (userAccount != null ? userAccount : "null"));
        System.out.println("  - currentPage: " + currentPage);
        System.out.println("  - pageSize: " + pageSize);
        System.out.println("  - universityName: " + (universityName != null ? universityName : "null"));
        
        if (userAccount == null) {
            System.out.println("  ❌ userAccount 为 null，返回 401");
            System.out.println("========== [PositionController] getPositionRecommendations 调用结束 - 返回 401 ==========");
            return Result.fail("请先登录");
        }
        
        System.out.println("  ✅ Token 验证通过，userAccount: " + userAccount);
        
        // 使用推荐服务获取分页结果（支持按学校名称过滤）
        System.out.println("\n【调用 Service】");
        Page<PositionVO> resultPage = positionRecommendationService.getPositionRecommendations(
            userAccount, new Page<>(currentPage, pageSize), universityName);
        
        System.out.println("\n【Service 返回结果】");
        System.out.println("  - 总记录数：" + resultPage.getTotal());
        System.out.println("  - 总页数：" + resultPage.getPages());
        System.out.println("  - 当前页：" + resultPage.getCurrent());
        System.out.println("  - 每页大小：" + resultPage.getSize());
        System.out.println("  - 返回记录数：" + resultPage.getRecords().size());
        
        System.out.println("\n========== [PositionController] getPositionRecommendations 调用结束 - 成功返回 ==========\n");
        
        return Result.ok(resultPage);
    }

    /**
     * 基于位置的岗位推荐
     */
    @PostMapping("/getPositionByLocation")
    public Result<List<PositionVO>> getPositionByLocation(
            @RequestParam("latitude") Double latitude,
            @RequestParam("longitude") Double longitude,
            @RequestParam("maxDistance") Double maxDistance){
        System.out.println(latitude+"+"+longitude);
        List<PositionVO> positions = positionRecommendationService.getLocationRecommendations(latitude, longitude, maxDistance);
        
        if (positions == null) {
            return Result.fail("参数错误");
        }
        
        System.out.println("=== 按地址推送请求 ===");
        System.out.println("用户位置：纬度=" + latitude + ", 经度=" + longitude);
        System.out.println("最大距离：" + maxDistance + "米");
        System.out.println("匹配岗位数：" + positions.size());
        for (PositionVO p : positions) {
            System.out.println("岗位 ID: " + p.getId() + ", 距离：" + String.format("%.2f", p.getDistance()) + "米");
        }
        System.out.println("========================");
        
        return Result.ok(positions);
    }

    // ========== 岗位管理接口 ==========

    /**
     * 企业查询自己的岗位列表
     */
    @GetMapping("/getPositionListByEmployer")
    public Result<IPage<PositionVO>> getPositionListByEmployer(
            HttpServletRequest request,
            @RequestParam(value = "currentPage", defaultValue = "1") Integer currentPage,
            @RequestParam(value = "pageSize", defaultValue = "8") Integer pageSize){
            
        // 从 Token 解析获取当前登录企业用户
        String employerAccount = (String) request.getAttribute("currentUserAccount");
        if (employerAccount == null) {
            return Result.fail("请先登录");
        }
        
        // 验证用户类型（只有企业用户才能查看自己的岗位）
        Integer userType = (Integer) request.getAttribute("currentUserType");
        if (userType == null || userType != 2) {  // 2 = 企业用户
            return Result.fail("权限不足");
        }
        
        // 使用 PositionManagementService 返回该企业的岗位列表
        IPage<PositionVO> positionPage = positionManagementService.getPositionListByEmployerAccountWithPagination(
            employerAccount, currentPage, pageSize);
        return Result.ok(positionPage);
    }
    
    /**
     * 分页获取岗位列表
     */
    @GetMapping("/getPositionListByPage")
    public Result<IPage<PositionVO>> getPositionListByPage(
            HttpServletRequest request,
            @RequestParam(value = "currentPage", defaultValue = "1") Integer currentPage,
            @RequestParam(value = "pageSize", defaultValue = "8") Integer pageSize){
        
        // 从 Token 解析获取当前登录用户
        String userAccount = (String) request.getAttribute("currentUserAccount");
        if (userAccount == null) {
            return Result.fail("请先登录");
        }
        
        // 调用推荐算法，返回个性化岗位列表
        IPage<PositionVO> positionPage = positionManagementService.getAllPositionsPaged(currentPage, pageSize);
        return Result.ok(positionPage);
    }

    /**
     * 保存岗位（创建或更新）
     */
    @PostMapping("/savePosition")
    public Result<String> savePosition(
            HttpServletRequest request,
            @RequestParam(value = "positionId", required = false) Integer positionId,
            @RequestParam(value = "salaryMin", required = false) Integer salaryMin,
            @RequestParam(value = "salaryMax", required = false) Integer salaryMax,
            @RequestParam(value = "salaryUnit", required = false, defaultValue = "月") String salaryUnit,
            @RequestParam("jobDescription") String jobDescription,
            @RequestParam(value = "workLocation", required = false, defaultValue = "") String workLocation,
            @RequestParam(value = "universityName", required = false) String universityName,
            @RequestParam(value = "latitude", required = false) Double latitude,
            @RequestParam(value = "longitude", required = false) Double longitude
    ){
        // 从 Token 解析获取当前登录用户（必须是企业用户）
        String employerAccount = (String) request.getAttribute("currentUserAccount");
        if (employerAccount == null) {
            return Result.fail("请先登录");
        }
        
        // 验证用户类型（只有企业用户才能发布岗位）
        Integer userType = (Integer) request.getAttribute("currentUserType");
        if (userType == null || userType != 2) {  // 2 = 企业用户
            return Result.fail("只有企业用户才能发布岗位");
        }
        
        // 1. 参数验证
        if (employerAccount == null || employerAccount.trim().isEmpty()) {
            return Result.badRequest("雇主账号不能为空");
        }
        
        // 使用公共验证方法校验岗位数据
        String errorMsg = ValidationUtils.validatePositionData(jobDescription, salaryMin, salaryMax, workLocation, latitude, longitude);
        if (errorMsg != null) {
            return Result.badRequest(errorMsg);
        }
        // 2. 调用 Service 执行业务逻辑
        String msg;
        if (positionId != null) {
            // 更新岗位
            msg = positionManagementService.updatePositionById(positionId, salaryMin, salaryMax, jobDescription, workLocation, universityName, latitude, longitude);
        } else {
            // 创建岗位
            msg = positionManagementService.createPosition(employerAccount, salaryMin, salaryMax, jobDescription, workLocation, universityName, latitude, longitude);
        }
        return msg.endsWith("成功") ? Result.ok(msg) : Result.fail(msg);
    }

    /**
     * 删除岗位
     */
    @PostMapping("/deletePosition")
    public Result<String> deletePosition(
            HttpServletRequest request,
            @RequestParam("positionId") Integer positionId){
        
        // 从 Token 解析获取当前登录用户（必须是企业用户）
        String employerAccount = (String) request.getAttribute("currentUserAccount");
        if (employerAccount == null) {
            return Result.fail("请先登录");
        }
        
        // 验证用户类型（只有企业用户才能删除岗位）
        Integer userType = (Integer) request.getAttribute("currentUserType");
        if (userType == null || userType != 2) {  // 2 = 企业用户
            return Result.fail("只有企业用户才能删除岗位");
        }
        
        String msg = positionManagementService.deletePositionById(positionId);
        return msg.equals("删除岗位成功") ? Result.ok(msg) : Result.fail(msg);
    }
}