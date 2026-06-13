package com.md.basePlatform.controller;

import com.md.basePlatform.common.Result;
import com.md.basePlatform.dto.LoginRequest;
import com.md.basePlatform.dto.UserDTO;
import com.md.basePlatform.exception.BusinessException;
import com.md.basePlatform.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.DisabledAccountException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器
 * 提供用户登录和信息查询 RESTful API 接口
 * 路径: /api/auth
 */
@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    /**
     * 用户登录
     * @param request 登录请求（包含用户名和密码）
     * @return 登录成功用户信息
     */
    @PostMapping("/login")
    public ResponseEntity<Result<UserDTO>> login(@Valid @RequestBody LoginRequest request) {
        log.info("登录请求: username={}", request.getUsername());
        Subject subject = SecurityUtils.getSubject();
        UsernamePasswordToken token = new UsernamePasswordToken(request.getUsername(), request.getPassword());
        try {
            subject.login(token);
        } catch (UnknownAccountException | IncorrectCredentialsException e) {
            throw BusinessException.unauthorized("用户名或密码错误");
        } catch (DisabledAccountException e) {
            throw BusinessException.unauthorized("账号已被禁用");
        }
        UserDTO user = userService.findByUsername(request.getUsername());
        return ResponseEntity.status(HttpStatus.OK)
                .body(Result.success(user, "登录成功"));
    }

    /**
     * 根据 ID 查询用户信息
     * @param id 用户 ID
     * @return 用户信息
     */
    @GetMapping("/user/{id}")
    public ResponseEntity<Result<UserDTO>> getUserInfo(@PathVariable Long id) {
        UserDTO user = userService.findById(id);
        return ResponseEntity.ok(Result.success(user));
    }
}