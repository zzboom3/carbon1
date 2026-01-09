package com.example.demo.service.impl;

import com.example.demo.dto.LoginDTO;
import com.example.demo.dto.RegisterDTO;
import com.example.demo.entity.User;
import com.example.demo.mapper.UserMapper;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    // --- 1. 注册 (保持你原有的逻辑) ---
    @Override
    public Boolean register(RegisterDTO registerDTO) {
        // 1. 检查用户名是否已存在
        User existUser = userMapper.selectByUsername(registerDTO.getUsername());
        if (existUser != null) {
            return false; // 用户名已存在
        }
        // 2. 直接使用明文密码（去掉加密步骤）
        User user = new User();
        user.setUsername(registerDTO.getUsername());
        user.setPassword(registerDTO.getPassword()); // 明文存储

        // 这里的 insert 对应 Mapper 中的 @Insert 语句
        int rows = userMapper.insert(user);
        return rows > 0;
    }

    // --- 2. 登录 (保持你原有的逻辑) ---
    @Override
    public User login(LoginDTO loginDTO) {
        // 1. 根据用户名查询用户
        User user = userMapper.selectByUsername(loginDTO.getUsername());
        if (user == null) {
            return null; // 用户名不存在
        }
        // 2. 直接对比明文密码（去掉加密校验）
        boolean matches = loginDTO.getPassword().equals(user.getPassword());
        if (!matches) {
            return null; // 密码错误
        }
        // 3. 密码正确，返回用户信息（脱敏）
        user.setPassword(null);
        return user;
    }

    // --- 3. 【新增】获取用户信息 ---
    @Override
    public User getUserInfo(String username) {
        // 直接复用 Mapper 中已有的 selectByUsername
        User user = userMapper.selectByUsername(username);

        if (user != null) {
            user.setPassword(null); // 安全起见，不返回密码

            // 如果角色还没填，给个默认值，防止前端显示空
            if (user.getRole() == null || user.getRole().isEmpty()) {
                user.setRole("普通用户");
            }
        }
        return user;
    }

    // --- 4. 【新增】更新用户信息 ---
    @Override
    public User updateUserInfo(User user) {
        // 调用 Mapper 中我们手动写的 updateById SQL
        userMapper.updateById(user);

        // 直接返回更新后的对象
        return user;
    }
}