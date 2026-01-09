package com.example.demo.service;

import com.example.demo.dto.LoginDTO;
import com.example.demo.dto.RegisterDTO;
import com.example.demo.entity.User;

public interface UserService {
    // 注册
    Boolean register(RegisterDTO registerDTO);

    // 登录
    User login(LoginDTO loginDTO);

    // --- 【新增】 ---
    User getUserInfo(String username);

    User updateUserInfo(User user);
}