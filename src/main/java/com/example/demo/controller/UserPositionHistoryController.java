package com.example.demo.controller;

import com.example.demo.common.Result;
import com.example.demo.service.UserPositionHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/history")
@CrossOrigin("*")
public class UserPositionHistoryController {

    @Autowired
    private UserPositionHistoryService userPositionHistoryService;

    /**
     * 记录用户浏览岗位
     */
    @PostMapping("/recordBrowse")
    public Result<String> recordPositionBrowse(
            @RequestParam("userAccount") String userAccount,
            @RequestParam("positionId") Integer positionId
    ) {
        String msg = userPositionHistoryService.recordPositionBrowse(userAccount, positionId);
        return msg.equals("浏览记录保存成功") ? Result.ok(msg) : Result.fail(msg);
    }
    
    /**
     * 记录用户投递岗位
     */
    @PostMapping("/recordDelivery")
    public Result<String> recordPositionDelivery(
            @RequestParam("userAccount") String userAccount,
            @RequestParam("positionId") Integer positionId
    ) {
        String msg = userPositionHistoryService.recordPositionDelivery(userAccount, positionId);
        return msg.equals("投递记录保存成功") ? Result.ok(msg) : Result.fail(msg);
    }
    
    /**
     * 记录用户通过筛选/面试
     */
    @PostMapping("/recordPass")
    public Result<String> recordPositionPass(
            @RequestParam("userAccount") String userAccount,
            @RequestParam("positionId") Integer positionId
    ) {
        String msg = userPositionHistoryService.recordPositionPass(userAccount, positionId);
        return msg.equals("通过记录保存成功") ? Result.ok(msg) : Result.fail(msg);
    }
}