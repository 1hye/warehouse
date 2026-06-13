package com.md.basePlatform.config.shiro;

import jakarta.servlet.Filter;
import lombok.RequiredArgsConstructor;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Apache Shiro 安全框架配置类
 *
 * 核心职责：
 * 1. 创建 SecurityManager — Shiro 的全局安全管理器，协调认证与授权
 * 2. 配置 Realm — 将 CustomRealm 注入 SecurityManager，实现用户身份验证
 * 3. 定义 URL 过滤器链 — 指定哪些路径需要登录、哪些可以匿名访问
 * 4. 注册 Shiro 过滤器到 Servlet 容器 — 确保所有请求先经过 Shiro 过滤
 *
 * 过滤器链规则说明：
 *   anon   — 允许匿名访问，无需登录
 *   authc  — 需要登录认证才能访问
 *   规则按顺序匹配，优先使用先定义的规则
 */
@Configuration
@RequiredArgsConstructor
public class ShiroConfig {

    /**
     * 注入自定义 Realm（通过构造函数注入，由 Spring 自动提供）
     * CustomRealm 中实现了具体的密码比对逻辑
     */
    private final CustomRealm customRealm;

    /**
     * 配置 Shiro Bean 生命周期处理器
     *
     * 作用：让 Shiro 框架中的 Bean（如 SecurityManager、Realm）
     * 能够正确地执行 init() 和 destroy() 生命周期方法，
     * 避免因 Spring 容器管理方式不同导致的初始化异常。
     *
     * 注意：必须声明为 static，否则 Spring 的 BeanPostProcessor
     * 检测机制会报 "not eligible for auto-proxying" 警告。
     *
     * @return LifecycleBeanPostProcessor 实例
     */
    @Bean
    public static LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
        return new LifecycleBeanPostProcessor();
    }

    /**
     * 配置默认 Web 安全管理器
     *
     * DefaultWebSecurityManager 是 Shiro 的核心协调器，
     * 它串联了以下组件：
     *   - Realm：实际执行用户查询和密码验证的地方
     *   - SessionManager：管理用户会话（默认使用 Servlet 容器会话）
     *   - CacheManager：缓存认证和授权信息（本系统未启用）
     *
     * 此处将 customRealm 注入 SecurityManager，使 Shiro 知道
     * 去哪个数据源查用户信息。
     *
     * @return 配置完成的 DefaultWebSecurityManager
     */
    @Bean
    public DefaultWebSecurityManager securityManager() {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setRealm(customRealm);
        return securityManager;
    }

    /**
     * 启用 Shiro 注解支持
     *
     * 作用：开启 @RequiresRoles、@RequiresPermissions、
     * @RequiresAuthentication 等注解的解析能力。
     * 当前系统暂未使用细粒度权限注解，但保留此配置为后续扩展做准备。
     *
     * @param securityManager 已配置好的 SecurityManager
     * @return AuthorizationAttributeSourceAdvisor 实例
     */
    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(SecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor advisor = new AuthorizationAttributeSourceAdvisor();
        advisor.setSecurityManager(securityManager);
        return advisor;
    }

    /**
     * 配置 Shiro 过滤器工厂，定义 URL 拦截规则
     *
     * 过滤器链含义（按顺序匹配，一旦匹配即生效）：
     * ┌─────────────────────────────────┬──────────┬──────────────────────────┐
     * │ URL 路径                        │ 过滤器   │ 说明                     │
     * ├─────────────────────────────────┼──────────┼──────────────────────────┤
     * │ /api/auth/login                 │ anon     │ 登录接口，允许匿名访问   │
     * │ /api/**                         │ jsonAuth │ 其他 API 接口需登录      │
     * └─────────────────────────────────┴──────────┴──────────────────────────┘
     *
     * jsonAuth 为自定义过滤器，返回 401 JSON 而不是跳转 login.jsp。
     * 注意：anon 规则必须写在 jsonAuth 之前，否则 jsonAuth 会优先拦截。
     * 使用 LinkedHashMap 保证规则插入顺序与迭代顺序一致。
     *
     * @param securityManager 已配置好的 SecurityManager
     * @return 配置完成的 ShiroFilterFactoryBean
     */
    @Bean
    public ShiroFilterFactoryBean shiroFilterFactoryBean(SecurityManager securityManager) {
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        shiroFilterFactoryBean.setSecurityManager(securityManager);

        /* 注册自定义过滤器，替换默认的 authc */
        Map<String, Filter> filters = new LinkedHashMap<>();
        filters.put("jsonAuth", new JsonAuthFilter());
        shiroFilterFactoryBean.setFilters(filters);

        /* URL 拦截规则 */
        Map<String, String> filterChainDefinitionMap = new LinkedHashMap<>();

        /* 登录接口允许未登录访问 */
        filterChainDefinitionMap.put("/api/auth/login", "anon");

        /* 其余所有 /api/** 接口必须登录后才能访问（返回 401 JSON） */
        filterChainDefinitionMap.put("/api/**", "jsonAuth");

        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);
        return shiroFilterFactoryBean;
    }

    /**
     * 将 Shiro 过滤器注册到 Servlet 容器
     *
     * 为什么要手动注册：
     * Spring Boot 不会自动注册 Shiro 的过滤器，必须通过
     * FilterRegistrationBean 手动添加到 Servlet 过滤器链中。
     *
     * 设置 order=1 确保 Shiro 过滤器在 Spring 的其它过滤器
     * （如 CorsFilter、CharacterEncodingFilter）之前执行，
     * 这样请求到达 Controller 之前已经完成身份认证。
     *
     * @param shiroFilterFactoryBean Shiro 过滤器工厂
     * @return 注册完成的 FilterRegistrationBean
     * @throws Exception 如果获取 Shiro 过滤器实例失败
     */
    @SuppressWarnings("unchecked")
    @Bean
    public FilterRegistrationBean<Filter> filterRegistrationBean(ShiroFilterFactoryBean shiroFilterFactoryBean) throws Exception {
        FilterRegistrationBean<Filter> registration = new FilterRegistrationBean<>();
        registration.setFilter((Filter) shiroFilterFactoryBean.getObject());
        registration.addUrlPatterns("/*");
        registration.setName("shiroFilter");
        registration.setOrder(1);
        return registration;
    }
}