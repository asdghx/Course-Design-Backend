package com.example.demo.controller;

import com.example.demo.common.Result;
import com.example.demo.service.UserPositionHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 用户岗位浏览历史相关API控制器
 * 记录用户浏览岗位的历史信息
 */
@RestController
@RequestMapping("/api/history")
@CrossOrigin("*")
public class UserPositionHistoryController {

    @Autowired
    private UserPositionHistoryService userPositionHistoryService;

    /**
     * 记录岗位浏览历史接口
     */
    @PostMapping("/recordBrowse")
    public Result<String> recordPositionBrowse(
            @RequestParam("userAccount") String userAccount,
            @RequestParam("positionId") Integer positionId
    ) {
        String msg = userPositionHistoryService.recordPositionBrowse(userAccount, positionId);
        return msg.equals("浏览记录保存成功") ? Result.ok(msg) : Result.fail(msg);
    }
}