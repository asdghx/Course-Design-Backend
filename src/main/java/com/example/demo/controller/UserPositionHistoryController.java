package com.example.demo.controller;

import com.example.demo.common.Result;
import com.example.demo.common.ValidationUtils;
import com.example.demo.service.UserPositionHistoryService;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * 用户行为历史控制器
 */
@RestController
@RequestMapping("/api/history")
@CrossOrigin("*")
public class UserPositionHistoryController {
    private final UserPositionHistoryService userPositionHistoryService;

    public UserPositionHistoryController(UserPositionHistoryService userPositionHistoryService) {
        this.userPositionHistoryService = userPositionHistoryService;
    }

    /**
     * 记录浏览岗位
     */
    @PostMapping("/recordBrowse")
    public Result<String> recordPositionBrowse(
            HttpServletRequest request,
            @RequestParam("positionId") Integer positionId
    ) {
        // 从 Token 解析获取当前登录用户
        String userAccount = (String) request.getAttribute("currentUserAccount");
        if (userAccount == null) {
            return Result.fail("请先登录");
        }
        
        // 参数验证
        String errorMsg = ValidationUtils.validateUserBehaviorParams(positionId);
        if (errorMsg != null) {
            return Result.badRequest(errorMsg);
        }
        
        String msg = userPositionHistoryService.recordPositionBrowse(userAccount, positionId);
        return msg.equals("浏览记录保存成功") ? Result.ok(msg) : Result.fail(msg);
    }
    
    /**
     * 记录投递岗位
     */
    @PostMapping("/recordDelivery")
    public Result<String> recordPositionDelivery(
            HttpServletRequest request,
            @RequestParam("positionId") Integer positionId
    ) {
        // 从 Token 解析获取当前登录用户
        String userAccount = (String) request.getAttribute("currentUserAccount");
        if (userAccount == null) {
            return Result.fail("请先登录");
        }
        
        // 参数验证
        String errorMsg = ValidationUtils.validateUserBehaviorParams(positionId);
        if (errorMsg != null) {
            return Result.badRequest(errorMsg);
        }
        
        String msg = userPositionHistoryService.recordPositionDelivery(userAccount, positionId);
        return msg.equals("投递记录保存成功") ? Result.ok(msg) : Result.fail(msg);
    }
    
    /**
     * 记录通过筛选
     */
    @PostMapping("/recordPass")
    public Result<String> recordPositionPass(
            HttpServletRequest request,
            @RequestParam("positionId") Integer positionId
    ) {
        // 从 Token 解析获取当前登录用户
        String userAccount = (String) request.getAttribute("currentUserAccount");
        if (userAccount == null) {
            return Result.fail("请先登录");
        }
        
        // 参数验证
        String errorMsg = ValidationUtils.validateUserBehaviorParams(positionId);
        if (errorMsg != null) {
            return Result.badRequest(errorMsg);
        }
        
        String msg = userPositionHistoryService.recordPositionPass(userAccount, positionId);
        return msg.equals("通过记录保存成功") ? Result.ok(msg) : Result.fail(msg);
    }
}