package com.example.uav;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.example.uav.repository")
public class UavApplication {

    public static void main(String[] args) {
        SpringApplication.run(UavApplication.class, args);
    }
}
