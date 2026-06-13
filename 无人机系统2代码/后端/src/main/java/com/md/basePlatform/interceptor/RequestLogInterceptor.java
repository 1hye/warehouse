package com.md.basePlatform.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Enumeration;

/**
 * 请求日志拦截器
 * 记录所有HTTP请求的详细信息，包括请求参数、响应状态和执行时长
 */
@Slf4j
public class RequestLogInterceptor implements HandlerInterceptor {
    
    /**
     * 存储请求开始时间，使用ThreadLocal确保线程安全
     */
    private static final ThreadLocal<Long> startTimeThreadLocal = new ThreadLocal<>();
    
    /**
     * 请求预处理
     * 在请求处理之前记录请求开始信息和参数
     * @param request HTTP请求对象
     * @param response HTTP响应对象
     * @param handler 处理器
     * @return true继续执行，false中断请求
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        long startTime = System.currentTimeMillis();
        startTimeThreadLocal.set(startTime);
        
        String method = request.getMethod();
        String url = request.getRequestURI();
        String queryString = request.getQueryString();
        String clientIp = getClientIp(request);
        
        StringBuilder params = new StringBuilder();
        Enumeration<String> parameterNames = request.getParameterNames();
        while (parameterNames.hasMoreElements()) {
            String paramName = parameterNames.nextElement();
            params.append(paramName).append("=").append(request.getParameter(paramName)).append("&");
        }
        if (params.length() > 0) {
            params.deleteCharAt(params.length() - 1);
        }
        
        log.info("【请求开始】 method={}, url={}, queryString={}, params={}, clientIp={}", 
                method, url, queryString, params.toString(), clientIp);
        
        return true;
    }
    
    /**
     * 请求后处理
     * 在视图渲染之前调用，当前为空实现
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, 
                          ModelAndView modelAndView) throws Exception {
    }
    
    /**
     * 请求完成处理
     * 在请求完成之后记录响应状态和执行时长
     * @param request HTTP请求对象
     * @param response HTTP响应对象
     * @param handler 处理器
     * @param ex 异常对象（如果有）
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, 
                                Object handler, Exception ex) throws Exception {
        long startTime = startTimeThreadLocal.get();
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        
        String method = request.getMethod();
        String url = request.getRequestURI();
        int statusCode = response.getStatus();
        
        log.info("【请求结束】 method={}, url={}, statusCode={}, duration={}ms", 
                method, url, statusCode, duration);
        
        startTimeThreadLocal.remove();
    }
    
    /**
     * 获取客户端真实IP地址
     * 优先从X-Forwarded-For头信息获取，其次从Proxy-Client-IP、WL-Proxy-Client-IP等头信息获取
     * @param request HTTP请求对象
     * @return 客户端IP地址
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        
        return ip;
    }
}