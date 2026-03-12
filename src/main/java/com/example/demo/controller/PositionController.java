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

import java.util.List;

/**
 * 岗位相关API控制器
 * 提供岗位推荐、企业岗位列表、岗位管理等功能
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

    // ==================== 岗位推荐相关接口 ====================

    /**
     * 用户推荐接口
     * 根据用户浏览历史提供个性化岗位推荐
     */
    @GetMapping("/getPositionRecommendations")
    public Result<Page<PositionVO>> getPositionRecommendations(
            @RequestParam("userAccount") String userAccount,
            @RequestParam(value = "currentPage", defaultValue = "1") Integer currentPage,
            @RequestParam(value = "pageSize", defaultValue = "8") Integer pageSize,
            @RequestParam(value = "universityName", required = false) String universityName){
        
        // 使用推荐服务获取分页结果（支持按学校名称过滤）
        Page<PositionVO> resultPage = positionRecommendationService.getPositionRecommendations(
            userAccount, new Page<>(currentPage, pageSize), universityName);
        
        return Result.ok(resultPage);
    }

    /**
     * 基于位置的岗位推荐接口
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

    // ==================== 岗位管理相关接口 ====================

    /**
     * 企业岗位列表接口
     * 根据企业账号查询发布的岗位（若未提供企业账号则返回所有岗位）
     */
    @GetMapping("/getPositionListByEmployer")
    public Result<IPage<PositionVO>> getPositionListByEmployer(
            @RequestParam(value = "employerAccount", required = false) String employerAccount,
            @RequestParam(value = "currentPage", defaultValue = "1") Integer currentPage,
            @RequestParam(value = "pageSize", defaultValue = "8") Integer pageSize){
            
        // 使用 PositionManagementService 返回岗位列表
        IPage<PositionVO> positionPage = positionManagementService.getPositionListByEmployerAccountWithPagination(
            employerAccount, currentPage, pageSize);
        return Result.ok(positionPage);
    }
    
    /**
     * 通用岗位列表分页接口
     * 返回所有岗位的分页数据
     */
    @GetMapping("/getPositionListByPage")
    public Result<IPage<PositionVO>> getPositionListByPage(
            @RequestParam(value = "currentPage", defaultValue = "1") Integer currentPage,
            @RequestParam(value = "pageSize", defaultValue = "8") Integer pageSize,
            @RequestParam(value = "userAccount", required = false) String userAccount){
        
        // 如果有 userAccount 参数，可能是需要推荐逻辑，否则返回普通分页
        // 这里保持兼容性，如果没有 userAccount 则返回所有岗位的分页数据
        IPage<PositionVO> positionPage = positionManagementService.getAllPositionsPaged(currentPage, pageSize);
        return Result.ok(positionPage);
    }

    /**
     * 保存岗位接口（创建或更新）
     * 如果提供 positionId 则更新，否则创建新岗位
     */
    @PostMapping("/savePosition")
    public Result<String> savePosition(
            @RequestParam(value = "positionId", required = false) Integer positionId,
            @RequestParam("employerAccount") String employerAccount,
            @RequestParam(value = "salaryMin", required = false) Integer salaryMin,
            @RequestParam(value = "salaryMax", required = false) Integer salaryMax,
            @RequestParam("jobDescription") String jobDescription,
            @RequestParam(value = "workLocation", required = false, defaultValue = "") String workLocation,
            @RequestParam(value = "universityName", required = false) String universityName,
            @RequestParam(value = "latitude", required = false) Double latitude,
            @RequestParam(value = "longitude", required = false) Double longitude
    ){
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
     * 删除岗位接口
     */
    @PostMapping("/deletePosition")
    public Result<String> deletePosition(
            @RequestParam("positionId") Integer positionId){
        String msg = positionManagementService.deletePositionById(positionId);
        return msg.equals("删除岗位成功") ? Result.ok(msg) : Result.fail(msg);
    }
}