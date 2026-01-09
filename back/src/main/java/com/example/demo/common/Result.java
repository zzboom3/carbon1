package com.example.demo.common;

import lombok.Data;

// 前后端交互的统一响应格式
@Data
public class Result<T> {
    // 响应码：200成功，500失败，400参数错误，401未登录
    private Integer code;
    // 响应消息
    private String msg;
    // 响应数据
    private T data;

    // 手动添加全参构造方法（关键修复点）
    public Result(Integer code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    // 成功响应（无数据）
    public static <T> Result<T> success() {
        return new Result<>(200, "操作成功", null);
    }

    // 成功响应（有数据）
    public static <T> Result<T> success(T data) {
        return new Result<>(200, "操作成功", data);
    }

    // 失败响应
    public static <T> Result<T> error(String msg) {
        return new Result<>(500, msg, null);
    }

    // 参数错误响应
    public static <T> Result<T> badRequest(String msg) {
        return new Result<>(400, msg, null);
    }
}