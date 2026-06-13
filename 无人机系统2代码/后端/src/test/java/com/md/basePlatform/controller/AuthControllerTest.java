package com.md.basePlatform.controller;

import com.md.basePlatform.dto.LoginRequest;
import com.md.basePlatform.dto.UserDTO;
import com.md.basePlatform.exception.BusinessException;
import com.md.basePlatform.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.DisabledAccountException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @Test
    void testLogin_Success() throws Exception {
        LoginRequest request = new LoginRequest("admin", "admin123");
        UserDTO user = UserDTO.builder()
                .id(1L).username("admin")
                .build();

        Subject mockSubject = mock(Subject.class);

        try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
            securityUtils.when(SecurityUtils::getSubject).thenReturn(mockSubject);

            when(userService.findByUsername("admin")).thenReturn(user);

            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data.username").value("admin"))
                    .andExpect(jsonPath("$.message").value("登录成功"));
        }
    }

    @Test
    void testLogin_WrongCredentials() throws Exception {
        LoginRequest request = new LoginRequest("admin", "wrongpass");

        Subject mockSubject = mock(Subject.class);
        doThrow(new IncorrectCredentialsException()).when(mockSubject).login(any(UsernamePasswordToken.class));

        try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
            securityUtils.when(SecurityUtils::getSubject).thenReturn(mockSubject);

            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.code").value(401))
                    .andExpect(jsonPath("$.message").value("用户名或密码错误"));
        }
    }

    @Test
    void testLogin_DisabledAccount() throws Exception {
        LoginRequest request = new LoginRequest("disabled", "pass123");

        Subject mockSubject = mock(Subject.class);
        doThrow(new DisabledAccountException()).when(mockSubject).login(any(UsernamePasswordToken.class));

        try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
            securityUtils.when(SecurityUtils::getSubject).thenReturn(mockSubject);

            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.code").value(401))
                    .andExpect(jsonPath("$.message").value("账号已被禁用"));
        }
    }

    @Test
    void testLogin_ValidationError() throws Exception {
        LoginRequest request = new LoginRequest("", "");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetUserInfo_Success() throws Exception {
        UserDTO user = UserDTO.builder()
                .id(1L).username("admin")
                .build();

        when(userService.findById(1L)).thenReturn(user);

        mockMvc.perform(get("/api/auth/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.username").value("admin"));
    }

    @Test
    void testGetUserInfo_NotFound() throws Exception {
        when(userService.findById(9999L))
                .thenThrow(BusinessException.notFound("用户不存在"));

        mockMvc.perform(get("/api/auth/user/9999"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(404))
                .andExpect(jsonPath("$.message").value("用户不存在"));
    }
}