package com.example.demo.controller;

import com.example.demo.common.Result;
import com.example.demo.entity.Position;
import com.example.demo.service.PositionManagementService;
import com.example.demo.service.PositionRecommendationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 岗位相关API控制器
 * 提供岗位推荐、企业岗位列表、岗位管理等功能
 */
@RestController
@RequestMapping("/api/position")
@CrossOrigin("*")
public class PositionController {

    @Autowired
    PositionManagementService positionManagementService;
    
    @Autowired
    PositionRecommendationService positionRecommendationService;
    
    /**
     * 用户推荐接口
     * 根据用户浏览历史提供个性化岗位推荐
     */
    @GetMapping("/getPositionRecommendations")
    public Result<com.baomidou.mybatisplus.extension.plugins.pagination.Page<Position>> getPositionRecommendations(
            @RequestParam("userAccount") String userAccount,
            @RequestParam(value = "currentPage", defaultValue = "1") Integer currentPage,
            @RequestParam(value = "pageSize", defaultValue = "8") Integer pageSize){
        
        // 创建分页对象
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<Position> page = 
            new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(currentPage, pageSize);
        
        // 使用推荐服务获取分页结果
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<Position> resultPage = 
            (com.baomidou.mybatisplus.extension.plugins.pagination.Page<Position>) 
            positionRecommendationService.getRecommendationsWithPage(userAccount, page);
        
        return Result.ok(resultPage);
    }
    
    /**
     * 企业岗位列表接口
     * 根据企业账号查询发布的岗位（若未提供企业账号则返回所有岗位）
     */
    @GetMapping("/getPositionListByEmployer")
    public Result<com.baomidou.mybatisplus.core.metadata.IPage<Position>> getPositionListByEmployer(
            @RequestParam(value = "employerAccount", required = false) String employerAccount,
            @RequestParam(value = "currentPage", defaultValue = "1") Integer currentPage,
            @RequestParam(value = "pageSize", defaultValue = "8") Integer pageSize){
        
        // 使用PositionManagementService返回岗位列表
        // 如果employerAccount为空，返回所有岗位；否则返回该企业发布的岗位
        com.baomidou.mybatisplus.core.metadata.IPage<Position> positionPage = positionManagementService.getPositionListByEmployerAccountWithPagination(employerAccount != null ? employerAccount : "", currentPage, pageSize);
        return Result.ok(positionPage);
    }
    
    /**
     * 通用岗位列表分页接口
     * 返回所有岗位的分页数据
     */
    @GetMapping("/getPositionListByPage")
    public Result<com.baomidou.mybatisplus.core.metadata.IPage<Position>> getPositionListByPage(
            @RequestParam(value = "currentPage", defaultValue = "1") Integer currentPage,
            @RequestParam(value = "pageSize", defaultValue = "8") Integer pageSize,
            @RequestParam(value = "userAccount", required = false) String userAccount){
        
        // 如果有userAccount参数，可能是需要推荐逻辑，否则返回普通分页
        // 这里保持兼容性，如果没有userAccount则返回所有岗位的分页数据
        com.baomidou.mybatisplus.core.metadata.IPage<Position> positionPage = positionManagementService.getAllPositionsPaged(currentPage, pageSize);
        return Result.ok(positionPage);
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

    /**
     * 创建岗位接口
     */
    @PostMapping("/createPosition")
    public Result<String> createPosition(
            @RequestParam("employerAccount") String employerAccount,
            @RequestParam("employmentType") String employmentType,
            @RequestParam("salaryRange") String salaryRange,
            @RequestParam("jobDescription") String jobDescription,
            @RequestParam(value = "workLocation", required = false, defaultValue = "") String workLocation,
            @RequestParam(value = "experienceRequirement", required = false, defaultValue = "") String experienceRequirement,
            @RequestParam(value = "educationRequirement", required = false, defaultValue = "") String educationRequirement
    ){
        String msg = positionManagementService.createPosition(employerAccount, employmentType, salaryRange, jobDescription, workLocation, experienceRequirement, educationRequirement);
        return msg.equals("添加岗位成功") ? Result.ok(msg) : Result.fail(msg);

    }

    /**
     * 更新岗位接口
     */
    @PostMapping("/updatePosition")
    public Result<String> updatePosition(
            @RequestParam("positionId") Integer positionId,
            @RequestParam("employerAccount") String employerAccount,
            @RequestParam("employmentType") String employmentType,
            @RequestParam("salaryRange") String salaryRange,
            @RequestParam("jobDescription") String jobDescription,
            @RequestParam(value = "workLocation", required = false, defaultValue = "") String workLocation,
            @RequestParam(value = "experienceRequirement", required = false, defaultValue = "") String experienceRequirement,
            @RequestParam(value = "educationRequirement", required = false, defaultValue = "") String educationRequirement
    ){
        String msg = positionManagementService.updatePositionById(positionId, employerAccount, employmentType, salaryRange, jobDescription, workLocation, experienceRequirement, educationRequirement);
        return msg.equals("更新岗位成功") ? Result.ok(msg) : Result.fail(msg);

    }
}