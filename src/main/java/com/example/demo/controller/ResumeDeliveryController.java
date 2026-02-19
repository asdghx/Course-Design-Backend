package com.example.demo.controller;

import com.example.demo.common.Result;
import com.example.demo.entity.Employee;
import com.example.demo.entity.Position;
import com.example.demo.service.ResumeDeliveryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 简历投递相关API控制器
 */
@RestController
@RequestMapping("/api/resume-delivery")
@CrossOrigin("*")
public class ResumeDeliveryController {

    @Autowired
    private ResumeDeliveryService resumeDeliveryService;

    /**
     * 创建简历投递申请接口
     */
    @PostMapping("/createApplication")
    public Result<String> createApplication(
            @RequestParam("userAccount") String userAccount,
            @RequestParam("positionId") Long positionId) {
        
        String result = resumeDeliveryService.addResumeDelivery(userAccount, positionId);
        if (result.equals("简历投递成功")) {
            return Result.ok(result);
        } else {
            return Result.fail(result);
        }
    }

    /**
     * 根据用户账号查询投递的岗位详情
     * 实现：先查询岗位ID，再查询岗位详细信息
     */
    @GetMapping("/getPositionsByUserAccount")
    public Result<List<Position>> getPositionsByUserAccount(
            @RequestParam("userAccount") String userAccount) {
        
        if (userAccount == null || userAccount.trim().isEmpty()) {
            return Result.badRequest("用户账号不能为空");
        }
        
        List<Position> positions = resumeDeliveryService.getPositionsByUserAccount(userAccount);
        return Result.ok(positions);
    }

    /**
     * 根据岗位ID查询投递用户的简历详情
     * 实现：先查询用户ID，再查询用户简历详细信息
     */
    @GetMapping("/getEmployeesByPositionId")
    public Result<List<Employee>> getEmployeesByPositionId(
            @RequestParam("positionId") Long positionId) {
        
        if (positionId == null) {
            return Result.badRequest("岗位ID不能为空");
        }
        
        List<Employee> employees = resumeDeliveryService.getEmployeesByPositionId(positionId);
        return Result.ok(employees);
    }
}