package com.md.basePlatform.service;

import com.md.basePlatform.dto.UserDTO;

/**
 * 用户服务接口
 * 定义用户认证和查询操作
 */
public interface UserService {

    /**
     * 用户登录
     * @param username 用户名
     * @param password 密码（未加密）
     * @return 登录成功用户信息
     */
    UserDTO login(String username, String password);

    /**
     * 根据 ID 查询用户详情
     * @param id 用户 ID
     * @return 用户详细信息
     */
    UserDTO findById(Long id);

    /**
     * 根据用户名查询用户详情
     * @param username 用户名
     * @return 用户详细信息
     */
    UserDTO findByUsername(String username);
}