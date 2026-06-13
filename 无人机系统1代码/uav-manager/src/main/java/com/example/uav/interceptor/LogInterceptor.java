package com.example.uav.interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 请求日志拦截器 - 打印请求信息
 */
@Component
public class LogInterceptor implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(LogInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        long startTime = System.currentTimeMillis();
        request.setAttribute("startTime", startTime);

        log.info("=== 请求开始 ===");
        log.info("请求方法: {}", request.getMethod());
        log.info("请求路径: {}", request.getRequestURI());
        log.info("查询参数: {}", request.getQueryString());
        log.info("请求IP: {}", getIpAddr(request));
        log.info("User-Agent: {}", request.getHeader("User-Agent"));
        log.info("处理器: {}", handler.getClass().getSimpleName());
        log.info("==============");

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                           ModelAndView modelAndView) {
        long startTime = (Long) request.getAttribute("startTime");
        long endTime = System.currentTimeMillis();

        log.info("=== 请求结束 ===");
        log.info("请求路径: {}", request.getRequestURI());
        log.info("响应状态: {}", response.getStatus());
        log.info("处理耗时: {} ms", endTime - startTime);
        log.info("==============");
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
                                Exception ex) {
        if (ex != null) {
            log.error("请求异常: {}, 异常信息: {}", request.getRequestURI(), ex.getMessage());
        }
    }

    private String getIpAddr(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
