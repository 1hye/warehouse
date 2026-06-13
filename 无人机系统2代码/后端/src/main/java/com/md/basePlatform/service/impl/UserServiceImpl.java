package com.md.basePlatform.service.impl;

import com.md.basePlatform.domain.User;
import com.md.basePlatform.dto.UserDTO;
import com.md.basePlatform.exception.BusinessException;
import com.md.basePlatform.mapper.UserMapper;
import com.md.basePlatform.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 用户服务实现类
 * 实现用户认证和注册的业务逻辑
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public UserDTO login(String username, String password) {
        log.info("用户登录: username={}", username);

        User user = userMapper.selectByUsername(username);
        if (user == null) {
            throw BusinessException.unauthorized("用户名或密码错误");
        }

        String encryptedPassword = encryptPassword(password);
        if (!encryptedPassword.equals(user.getPassword())) {
            throw BusinessException.unauthorized("用户名或密码错误");
        }

        if (user.getStatus() == null || user.getStatus() != 1) {
            throw BusinessException.unauthorized("账号已被禁用");
        }

        log.info("用户登录成功: id={}, username={}", user.getId(), user.getUsername());
        return convertToDTO(user);
    }

    @Override
    public UserDTO findById(Long id) {
        log.info("查询用户详情: id={}", id);

        User user = userMapper.selectById(id);
        if (user == null) {
            throw BusinessException.notFound("用户不存在");
        }

        return convertToDTO(user);
    }

    @Override
    public UserDTO findByUsername(String username) {
        log.info("查询用户详情: username={}", username);

        User user = userMapper.selectByUsername(username);
        if (user == null) {
            throw BusinessException.notFound("用户不存在");
        }

        return convertToDTO(user);
    }

    private String encryptPassword(String password) {
        try {
            return DigestUtils.md5DigestAsHex(("baseplatform" + password + "md5salt").getBytes("UTF-8"));
        } catch (Exception e) {
            return DigestUtils.md5DigestAsHex(("baseplatform" + password + "md5salt").getBytes());
        }
    }

    private UserDTO convertToDTO(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .status(user.getStatus())
                .createTime(user.getCreateTime() != null ? user.getCreateTime().format(FORMATTER) : null)
                .updateTime(user.getUpdateTime() != null ? user.getUpdateTime().format(FORMATTER) : null)
                .build();
    }
}