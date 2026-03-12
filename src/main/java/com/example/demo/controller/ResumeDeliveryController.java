package com.example.demo.controller;

import com.example.demo.common.Result;
import com.example.demo.entity.Employee;
import com.example.demo.entity.Position;
import com.example.demo.service.ResumeDeliveryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.example.demo.entity.vo.UserDeliveryVO;
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
            @RequestParam("positionId") Integer positionId) {
        
        String result = resumeDeliveryService.addResumeDelivery(userAccount, positionId);
        if (result.endsWith("成功")) {
            return Result.ok(result);
        } else {
            return Result.fail(result);
        }
    }

    /**
     * 根据用户账号查询投递记录详情（包含岗位信息和投递状态）
     * 为了向前兼容，此接口返回增强的数据结构
     */
    @GetMapping("/getPositionsByUserAccount")
    public Result<List<UserDeliveryVO>> getPositionsByUserAccount(
            @RequestParam("userAccount") String userAccount) {
        
        if (userAccount == null || userAccount.trim().isEmpty()) {
            return Result.badRequest("用户账号不能为空");
        }
        
        List<UserDeliveryVO> deliveries = resumeDeliveryService.getDeliveriesWithPositionInfo(userAccount);
        return Result.ok(deliveries);
    }

    /**
     * 根据岗位ID查询投递用户的简历详情
     * 实现：先查询用户ID，再查询用户简历详细信息
     */
    @GetMapping("/getEmployeesByPositionId")
    public Result<List<Employee>> getEmployeesByPositionId(
            @RequestParam("positionId") Integer positionId) {
        
        if (positionId == null) {
            return Result.badRequest("岗位ID不能为空");
        }
        
        List<Employee> employees = resumeDeliveryService.getEmployeesByPositionId(positionId);
        return Result.ok(employees);
    }

    /**
     * 更新投递状态（统一接口）
     */
    @PutMapping("/updateDeliveryStatus")
  public Result<String> updateDeliveryStatus(
            @RequestParam("userAccount") String userAccount,
            @RequestParam("positionId") Integer positionId,
            @RequestParam("status") Integer status) {
        
        String result = resumeDeliveryService.updateDeliveryStatus(userAccount, positionId, status);
        if (result.startsWith("状态更新成功")) {
            return Result.ok(result);
        } else {
            return Result.fail(result);
        }
    }
}
