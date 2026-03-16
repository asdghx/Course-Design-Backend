package com.example.demo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web 配置类 - 拦截器配置
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private JwtInterceptor jwtInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor)
                // 拦截所有 /api/ 开头的请求
                .addPathPatterns("/api/**")
                // 排除不需要登录的接口（公开访问）
                .excludePathPatterns(
                        "/api/user/login",          // 登录
                        "/api/user/register",       // 注册
                        "/api/user/sendVerifyCode", // 发送验证码（修改密码第一步）
                        "/api/user/changePassword", // 修改密码（第二步）
                        "/api/article/getArticlePage",     // 获取文章列表
                        "/api/article/getArticleContent",  // 获取文章内容
                        "/api/position/getPositionByLocation",  // 基于位置推荐
                        "/api/search/searchKeyword"   // 搜索文章
                );
    }
}
