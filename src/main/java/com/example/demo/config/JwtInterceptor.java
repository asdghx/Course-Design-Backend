package com.example.demo.config;

import com.example.demo.common.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * JWT 认证拦截器
 * 拦截需要登录的 API 请求，验证 Token 有效性
 */
@Component
public class JwtInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        System.out.println("\n========== [JwtInterceptor] 拦截请求 ==========");
        
        // 1. 从请求头获取 Token
        String token = request.getHeader("Authorization");
        String requestURI = request.getRequestURI();
        
        System.out.println("【拦截器检查】");
        System.out.println("  - 请求 URL: " + requestURI);
        System.out.println("  - 请求方法：" + request.getMethod());
        
        // 2. 如果是 OPTIONS 请求（预检请求），直接放行
        if ("OPTIONS".equals(request.getMethod())) {
            System.out.println("  ⚠️ OPTIONS 预检请求，直接放行");
            return true;
        }
        
        // 处理 "Bearer " 前缀
        if (token != null && token.startsWith("Bearer ")) {
            System.out.println("  ℹ️ 检测到 Bearer 前缀，自动去除...");
            token = token.substring(7);  // 去掉 "Bearer " 前缀
        }
        
        // 3. 如果没有 Token，返回 401（要求登录）
        if (token == null || token.trim().isEmpty()) {
            System.out.println("  ❌ Header 中没有 Authorization");
            System.out.println("  ❌ 返回 401 - 请先登录");
            System.out.println("========== [JwtInterceptor] 拦截结束 ==========\n");
            
            response.setStatus(401);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":401,\"message\":\"请先登录\"}");
            return false;
        }
        
        System.out.println("  ✅ Authorization: " + token.substring(0, Math.min(50, token.length())) + "...");
        
        // 4. 验证 Token 有效性
        System.out.println("  🔍 验证 Token 有效性...");
        if (!jwtUtil.validateToken(token)) {
            System.out.println("  ❌ Token 无效或已过期");
            System.out.println("  ❌ 返回 401 - Token 无效或已过期，请重新登录");
            System.out.println("========== [JwtInterceptor] 拦截结束 ==========\n");
            
            // Token 无效或过期，返回 401
            response.setStatus(401);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":401,\"message\":\"Token 无效或已过期，请重新登录\"}");
            return false;
        }
        
        System.out.println("  ✅ Token 验证通过");
        
        // 5. 解析 Token，将用户信息存入请求属性（供后续使用）
        String userAccount = jwtUtil.getUserAccountFromToken(token);
        Integer userType = jwtUtil.getUserTypeFromToken(token);
        Long userId = jwtUtil.getUserIdFromToken(token);
        
        System.out.println("\n【Token 解析结果】");
        System.out.println("  ✅ userAccount: " + userAccount);
        System.out.println("  ✅ userType: " + userType);
        System.out.println("  ✅ userId: " + userId);
        
        request.setAttribute("currentUserAccount", userAccount);
        request.setAttribute("currentUserType", userType);
        request.setAttribute("currentUserId", userId);
        
        System.out.println("\n  ✅ 已将用户信息存入 request.setAttribute()");
        System.out.println("  ✅ 放行，继续执行 Controller");
        System.out.println("========== [JwtInterceptor] 拦截结束 ==========\n");
        
        // 6. 放行，继续执行后续处理
        return true;
    }
}
