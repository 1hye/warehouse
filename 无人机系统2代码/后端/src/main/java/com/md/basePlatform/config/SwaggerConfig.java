package com.md.basePlatform.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger API文档配置类
 * 配置OpenAPI 3.0规范的系统信息
 */
@Configuration
public class SwaggerConfig {
    
    /**
     * 配置OpenAPI文档信息
     * @return OpenAPI实例
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("无人机信息管理系统 API")
                        .version("1.0.0")
                        .description("无人机信息管理系统的 RESTful API 文档")
                        .contact(new Contact()
                                .name("开发团队")
                                .email("dev@example.com")));
    }
}