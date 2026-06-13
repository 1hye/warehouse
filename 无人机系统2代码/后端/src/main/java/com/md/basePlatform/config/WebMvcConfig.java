package com.md.basePlatform.config;

import com.md.basePlatform.interceptor.RequestLogInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC配置类
 * 配置拦截器和跨域映射
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    
    /**
     * 创建请求日志拦截器实例
     * @return RequestLogInterceptor实例
     */
    @Bean
    public RequestLogInterceptor requestLogInterceptor() {
        return new RequestLogInterceptor();
    }
    
    /**
     * 添加拦截器到拦截器注册表
     * 拦截所有/api/**路径的请求
     * @param registry 拦截器注册表
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(requestLogInterceptor())
                .addPathPatterns("/api/**");
    }
    
    /**
     * 配置跨域映射
     * 允许所有来源访问/api/**路径
     * @param registry 跨域注册表
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .maxAge(3600);
    }
}