package com.md.basePlatform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 无人机信息管理系统启动类
 * Spring Boot 应用入口，自动扫描 com.md.basePlatform 包下的组件
 */
@SpringBootApplication
public class BasePlatformApplication {

	public static void main(String[] args) {
		SpringApplication.run(BasePlatformApplication.class, args);
	}

}
