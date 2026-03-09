package com.example.demo.common;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

/**
 * MD5加密工具类
 * 提供密码加密、加盐加密等功能
 */
public class MD5Util {

    private static final String ALGORITHM = "MD5";

    /**
     * 生成随机盐值
     */
    public static String generateSalt() {
        Random random = new Random();
        return String.valueOf(random.nextInt(900000) + 100000); // 生成6位随机数
    }

    /**
     * 对密码加盐后进行MD5加密
     */
    public static String encryptWithSalt(String plainText, String salt) {
        try {
            MessageDigest md = MessageDigest.getInstance(ALGORITHM);
            String saltedPassword = plainText + salt;
            md.update(saltedPassword.getBytes());
            byte[] digest = md.digest();

            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b & 0xff));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5算法不可用", e);
        }
    }

    /**
     * 验证带盐的密码是否匹配
     */
    public static boolean verifyWithSalt(String plainPassword, String encryptedPassword, String salt) {
        String encryptedPlain = encryptWithSalt(plainPassword, salt);
        return encryptedPlain.equals(encryptedPassword);
    }
}