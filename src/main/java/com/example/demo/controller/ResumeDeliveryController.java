package com.example.demo.controller;

import com.example.demo.common.Result;
import com.example.demo.common.ValidationUtils;
import com.example.demo.entity.Employee;
import com.example.demo.entity.Position;
import com.example.demo.service.ResumeDeliveryService;
import org.springframework.web.bind.annotation.*;

import com.example.demo.entity.vo.UserDeliveryVO;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 简历投递控制器
 */
@RestController
@RequestMapping("/api/resume-delivery")
@CrossOrigin("*")
public class ResumeDeliveryController {
    private final ResumeDeliveryService resumeDeliveryService;

    public ResumeDeliveryController(ResumeDeliveryService resumeDeliveryService) {
        this.resumeDeliveryService = resumeDeliveryService;
    }

    /**
     * 创建投递申请
     */
    @PostMapping("/createApplication")
    public Result<String> createApplication(
            HttpServletRequest request,
            @RequestParam("positionId") Integer positionId) {
        
        // 从 Token 解析获取当前登录用户
        String userAccount = (String) request.getAttribute("currentUserAccount");
        if (userAccount == null) {
            return Result.fail("请先登录");
        }
        
        // 参数验证
        String errorMsg = ValidationUtils.validateResumeDeliveryParams(positionId);
        if (errorMsg != null) {
            return Result.badRequest(errorMsg);
        }
        
        String result = resumeDeliveryService.addResumeDelivery(userAccount, positionId);
        if (result.endsWith("成功")) {
            return Result.ok(result);
        } else {
            return Result.fail(result);
        }
    }

    /**
     * 查询用户投递记录
     */
    @GetMapping("/getPositionsByUserAccount")
    public Result<List<UserDeliveryVO>> getPositionsByUserAccount(
            HttpServletRequest request) {
        
        // 从 Token 解析获取当前登录用户
        String userAccount = (String) request.getAttribute("currentUserAccount");
        if (userAccount == null) {
            return Result.fail("请先登录");
        }
        
        List<UserDeliveryVO> deliveries = resumeDeliveryService.getDeliveriesWithPositionInfo(userAccount);
        return Result.ok(deliveries);
    }

    /**
     * 企业查询投递该岗位的简历
     */
    @GetMapping("/getEmployeesByPositionId")
    public Result<List<Employee>> getEmployeesByPositionId(
            HttpServletRequest request,
            @RequestParam("positionId") Integer positionId) {
            
        if (positionId == null) {
            return Result.badRequest("岗位 ID 不能为空");
        }
            
        // TODO: 这里需要验证是否是企业管理员，防止恶意查询
        // 暂时先不验证，后续可以添加权限检查
            
        List<Employee> employees = resumeDeliveryService.getEmployeesByPositionId(positionId);
        return Result.ok(employees);
    }

    /**
     * 更新投递状态
     */
    @PutMapping("/updateDeliveryStatus")
  public Result<String> updateDeliveryStatus(
            HttpServletRequest request,
            @RequestParam("positionId") Integer positionId,
            @RequestParam("status") Integer status) {
        
        // 从 Token 解析获取当前登录用户
        String userAccount = (String) request.getAttribute("currentUserAccount");
        if (userAccount == null) {
            return Result.fail("请先登录");
        }
        
        // 参数验证
        String errorMsg = ValidationUtils.validateUpdateDeliveryStatusParams(positionId, status);
        if (errorMsg != null) {
            return Result.badRequest(errorMsg);
        }
        
        String result = resumeDeliveryService.updateDeliveryStatus(userAccount, positionId, status);
        if (result.startsWith("状态更新成功")) {
            return Result.ok(result);
        } else {
            return Result.fail(result);
        }
    }

    /**
     * 删除投递记录
     */
    @DeleteMapping("/deleteDelivery")
    public Result<String> deleteDelivery(
            HttpServletRequest request,
            @RequestParam("positionId") Integer positionId
    ) {
        System.out.println("\n========== [ResumeDeliveryController] deleteDelivery 调用开始 ==========");
        
        // 从 Token 解析获取当前登录用户
        String userAccount = (String) request.getAttribute("currentUserAccount");
        System.out.println("【Token 解析结果】");
        System.out.println("  - userAccount: " + userAccount);
        System.out.println("  - positionId: " + positionId);
        
        if (userAccount == null) {
            System.out.println("  ❌ userAccount 为 null，返回 401");
            System.out.println("========== [ResumeDeliveryController] deleteDelivery 调用结束 - 返回 401 ==========");
            return Result.fail("请先登录");
        }
        
        // 参数验证
        if (positionId == null) {
            System.out.println("  ❌ positionId 为 null，返回 400");
            System.out.println("========== [ResumeDeliveryController] deleteDelivery 调用结束 - 返回 400 ==========");
            return Result.badRequest("岗位 ID 不能为空");
        }
        
        System.out.println("  ✅ 参数验证通过");
        
        // 调用 Service
        String result = resumeDeliveryService.deleteDelivery(userAccount, positionId);
        System.out.println("  Service 返回：" + result);
        
        if (result.endsWith("成功")) {
            System.out.println("========== [ResumeDeliveryController] deleteDelivery 调用结束 - 成功返回 ==========");
            return Result.ok(result);
        } else {
            System.out.println("========== [ResumeDeliveryController] deleteDelivery 调用结束 - 失败返回 ==========");
            return Result.fail(result);
        }
    }

    /**
     * 将投递记录标记为已读
     */
    @PostMapping("/markAsRead")
    public Result<Integer> markAsRead(HttpServletRequest request) {
        System.out.println("\n========== [ResumeDeliveryController] markAsRead 调用开始 ==========");
        
        // 从 Token 解析获取当前登录用户
        String userAccount = (String) request.getAttribute("currentUserAccount");
        System.out.println("【Token 解析结果】");
        System.out.println("  - userAccount: " + userAccount);
        
        if (userAccount == null) {
            System.out.println("  ❌ userAccount 为 null，返回 401");
            System.out.println("========== [ResumeDeliveryController] markAsRead 调用结束 - 返回 401 ==========");
            return Result.fail("请先登录");
        }
        
        System.out.println("  ✅ 参数验证通过");
        
        // 调用 Service
        int rows = resumeDeliveryService.markAllAsRead(userAccount);
        System.out.println("  Service 返回：更新了 " + rows + " 条记录");
        
        System.out.println("========== [ResumeDeliveryController] markAsRead 调用结束 - 成功返回 ==========");
        return Result.ok(rows);
    }

    /**
     * 统计未读更新数量
     */
    @GetMapping("/countUnreadUpdates")
    public Result<Integer> countUnreadUpdates(HttpServletRequest request) {
        System.out.println("\n========== [ResumeDeliveryController] countUnreadUpdates 调用开始 ==========");
        
        // 从 Token 解析获取当前登录用户
        String userAccount = (String) request.getAttribute("currentUserAccount");
        System.out.println("【Token 解析结果】");
        System.out.println("  - userAccount: " + userAccount);
        
        if (userAccount == null) {
            System.out.println("  ❌ userAccount 为 null，返回 401");
            System.out.println("========== [ResumeDeliveryController] countUnreadUpdates 调用结束 - 返回 401 ==========");
            return Result.fail("请先登录");
        }
        
        System.out.println("  ✅ 参数验证通过");
        
        // 调用 Service
        int count = resumeDeliveryService.countUnreadUpdates(userAccount);
        System.out.println("  Service 返回：未读更新数量 = " + count);
        
        System.out.println("========== [ResumeDeliveryController] countUnreadUpdates 调用结束 - 成功返回 ==========");
        return Result.ok(count);
    }
}
