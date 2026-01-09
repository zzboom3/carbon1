package com.example.demo.mapper;

import com.example.demo.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update; // 记得引入 Update
import org.springframework.stereotype.Repository;

@Repository
public interface UserMapper {

    // 1. 根据用户名查询用户 (原有)
    // 只要你的数据库表里加了 nickname, phone 等字段，SELECT * 就能把它们都查出来
    @Select("SELECT * FROM user WHERE username = #{username}")
    User selectByUsername(String username);

    // 2. 新增用户 (原有，用于注册)
    @Insert("INSERT INTO user (username, password) VALUES (#{username}, #{password})")
    int insert(User user);

    // 3. 【新增】根据 ID 更新用户信息
    // 这里我们手动写 SQL，把除了密码和用户名之外的字段都更新一遍
    @Update("UPDATE user SET nickname = #{nickname}, phone = #{phone}, " +
            "email = #{email}, role = #{role}, avatar = #{avatar} " +
            "WHERE id = #{id}")
    int updateById(User user);
}