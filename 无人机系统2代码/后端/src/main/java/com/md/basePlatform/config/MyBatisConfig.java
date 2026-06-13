package com.md.basePlatform.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis配置类
 * 配置MyBatis扫描Mapper接口的包路径
 */
@Configuration
@MapperScan("com.md.basePlatform.mapper")
public class MyBatisConfig {
    
}