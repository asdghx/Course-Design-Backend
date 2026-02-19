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

    @PostMapping("/recordBrowse")
    public Result<String> recordPositionBrowse(
            @RequestParam("userAccount") String userAccount,
            @RequestParam("positionId") Integer positionId
    ) {
        String msg = userPositionHistoryService.recordPositionBrowse(userAccount, positionId);
        return msg.equals("浏览记录保存成功") ? Result.ok(msg) : Result.fail(msg);
    }
}