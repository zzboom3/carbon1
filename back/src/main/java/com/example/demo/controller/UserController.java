package com.example.demo.controller;

import com.example.demo.common.Result;
import com.example.demo.dto.LoginDTO;
import com.example.demo.dto.RegisterDTO;
import com.example.demo.entity.User;
import com.example.demo.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class UserController {

    @Autowired
    private UserService userService;

    // --- 注册接口 (保持不变) ---
    @PostMapping("/register")
    public Result<?> register(@Valid @RequestBody RegisterDTO registerDTO) {
        Boolean success = userService.register(registerDTO);
        if (!success) {
            return Result.error("用户名已存在");
        }
        return Result.success();
    }

    // --- 登录接口 (保持不变) ---
    @PostMapping("/login")
    public Result<User> login(@Valid @RequestBody LoginDTO loginDTO) {
        User user = userService.login(loginDTO);
        if (user == null) {
            return Result.error("用户名或密码错误");
        }
        return Result.success(user);
    }

    // --- 【新增】获取当前用户信息 ---
    @GetMapping("/info")
    public Result<User> getUserInfo(@RequestParam String username) {
        // 直接调用 Service 层写好的逻辑
        // Service 内部会处理 SQL 查询、密码脱敏、默认角色设置等
        User user = userService.getUserInfo(username);
        return Result.success(user);
    }

    // --- 【新增】更新用户信息 ---
    @PostMapping("/update")
    public Result<User> updateUserInfo(@RequestBody User user) {
        // 简单校验
        if (user.getId() == null) {
            return Result.error("更新失败，用户ID不能为空");
        }
        // 直接调用 Service 层写好的逻辑
        User updatedUser = userService.updateUserInfo(user);
        return Result.success(updatedUser);
    }
}