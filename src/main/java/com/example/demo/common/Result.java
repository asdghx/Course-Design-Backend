package com.example.demo.common;

import lombok.Data;

@Data
public class Result<T> {
    private int code;
    private String msg;
    private T data;

    public static <T> Result<T> result(int code, String msg, T data) {
        Result<T> r = new Result<>();
        r.setCode(code);
        r.setMsg(msg);
        r.setData(data);
        return r;
    }

    public static <T> Result<T> ok(T data) {
        return result(200, "success", data);
    }

    public static <T> Result<T> ok(String msg) {
        return result(200, msg, null);
    }

    public static <T> Result<T> fail(String msg) {
        return result(500, msg, null);
    }

    public static <T> Result<T> badRequest(String msg) {
        return result(400, msg, null);
    }
}
