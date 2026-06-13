package com.md.basePlatform.config.shiro;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.shiro.web.filter.authc.UserFilter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 自定义 Shiro 认证过滤器
 *
 * 对未登录的 API 请求直接返回 401 JSON 响应，
 * 而非默认的 redirect 到 login.jsp。
 * 适用于前后端分离场景（前端 SPA 通过 Ajax 调用 API）。
 */
public class JsonAuthFilter extends UserFilter {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void redirectToLogin(ServletRequest request, ServletResponse response) throws IOException {
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        httpResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
        httpResponse.setContentType(MediaType.APPLICATION_JSON_VALUE);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("code", 401);
        result.put("message", "未登录，请先登录");
        result.put("data", null);

        httpResponse.getWriter().write(objectMapper.writeValueAsString(result));
    }
}