package com.example.demo.common;

import lombok.Data;

/**
 * 统一API响应结果封装类
 * 简化版本，保留最核心的功能
 */
@Data
public class Result<T> {
    private int code; // 状态码
    private String msg; // 响应消息
    private T data; // 响应数据

    // 核心构造方法
    public static <T> Result<T> result(int code, String msg, T data) {
        Result<T> r = new Result<>();
        r.setCode(code);
        r.setMsg(msg);
        r.setData(data);
        return r;
    }

    // 最常用的便捷方法
    public static <T> Result<T> ok(T data) {
        return result(200, "success", data);
    }

    public static <T> Result<T> ok(String msg) {
        return result(200, msg, null);
    }

    public static <T> Result<T> fail(String msg) {
        return result(500, msg, null);
    }

    // 常用HTTP状态码方法
    public static <T> Result<T> badRequest(String msg) {
        return result(400, msg, null);
    }
}
