package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;

public class LoginDTO {
    @NotBlank(message = "用户名不能为空")
    private String username;

    @NotBlank(message = "密码不能为空")
    private String password;

    // 用户名 getter 方法
    public String getUsername() {
        return username;
    }

    // 用户名 setter 方法
    public void setUsername(String username) {
        this.username = username;
    }

    // 密码 getter 方法
    public String getPassword() {
        return password;
    }

    // 密码 setter 方法
    public void setPassword(String password) {
        this.password = password;
    }
}