package com.example.demo.common;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT 工具类
 * 用于生成、解析和验证 Token
 */
@Component
public class JwtUtil {

    // 密钥（必须是 Base64 兼容的 ASCII 字符）
    private static final String SECRET_KEY = "gaojian2026parttimeplatformsecretkey";
    
    // Token 过期时间：7 天
    private static final long EXPIRATION_TIME = 7 * 24 * 60 * 60 * 1000;

    /**
     * 生成 JWT Token
     * 
     * @param userId 用户 ID（来自 user_login.id）
     * @param userAccount 用户账号（来自 user_login.user_account）
     * @param userType 用户类型（来自 user_login.user_type）
     * @return JWT Token 字符串
     */
    public String createToken(Long userId, String userAccount, Integer userType) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("userAccount", userAccount);
        claims.put("userType", userType);
        
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userAccount)  // 主题设为账号
                .setIssuedAt(new Date())  // 签发时间
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME)) // 过期时间
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY) // 签名算法
                .compact();
    }

    /**
     * 解析 JWT Token
     * 
     * @param token JWT Token 字符串
     * @return Claims 对象，包含用户信息
     */
    public Claims parseToken(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * 验证 Token 是否有效
     * 
     * @param token JWT Token 字符串
     * @return true=有效，false=无效或已过期
     */
    public boolean validateToken(String token) {
        try {
            Claims claims = parseToken(token);
            // 检查是否过期
            return claims.getExpiration().after(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 从 Token 中获取用户 ID
     */
    public Long getUserIdFromToken(String token) {
        Claims claims = parseToken(token);
        return claims.get("userId", Long.class);
    }

    /**
     * 从 Token 中获取用户账号
     */
    public String getUserAccountFromToken(String token) {
        Claims claims = parseToken(token);
        return claims.get("userAccount", String.class);
    }

    /**
     * 从 Token 中获取用户类型
     */
    public Integer getUserTypeFromToken(String token) {
        Claims claims = parseToken(token);
        return claims.get("userType", Integer.class);
    }
}