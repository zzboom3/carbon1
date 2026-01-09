package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RegisterDTO {
    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 20, message = "用户名长度需在3-20位之间")
    private String username;

    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度需在6-20位之间")
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