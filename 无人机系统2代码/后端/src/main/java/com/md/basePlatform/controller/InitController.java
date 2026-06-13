package com.md.basePlatform.controller;

import com.md.basePlatform.common.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.util.DigestUtils;

import javax.sql.DataSource;
import java.time.LocalDateTime;

/**
 * 初始化控制器
 * 提供数据库表初始化功能
 * 路径: /api/init
 */
@Slf4j
@RestController
@RequestMapping("/api/init")
@RequiredArgsConstructor
public class InitController {

    private final DataSource dataSource;

    /**
     * 初始化用户表和测试用户
     * 如果用户表不存在则创建，并插入测试用户admin
     * @return 初始化结果
     */
    @PostMapping("/user-table")
    public Result<String> initUserTable() {
        try {
            JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

            // 创建用户表
            String createTableSql = """
                CREATE TABLE IF NOT EXISTS sys_user (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    username VARCHAR(50) NOT NULL UNIQUE,
                    password VARCHAR(100) NOT NULL,
                    status INTEGER DEFAULT 1,
                    create_time DATETIME NOT NULL,
                    update_time DATETIME NOT NULL
                )
                """;
            jdbcTemplate.execute(createTableSql);
            log.info("sys_user 表创建成功");

            // 检查管理员用户是否存在
            Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM sys_user WHERE username = ?",
                Integer.class,
                "admin"
            );

            if (count == null || count == 0) {
                String encryptedPassword = DigestUtils.md5DigestAsHex(("baseplatform" + "admin123" + "md5salt").getBytes());
                String insertUserSql = """
                    INSERT INTO sys_user (username, password, status, create_time, update_time)
                    VALUES (?, ?, ?, ?, ?)
                    """;
                jdbcTemplate.update(insertUserSql,
                    "admin",
                    encryptedPassword,
                    1,
                    LocalDateTime.now(),
                    LocalDateTime.now()
                );
                log.info("测试用户 admin 创建成功");
                return Result.success("用户表和测试用户创建成功");
            } else {
                return Result.success("用户表已存在，测试用户已创建");
            }
        } catch (Exception e) {
            log.error("初始化用户表失败: {}", e.getMessage());
            return Result.error(500, "初始化失败: " + e.getMessage());
        }
    }

    /**
     * 初始化无人机表和测试数据
     * 如果无人机表不存在则创建，并插入测试无人机数据
     * @return 初始化结果
     */
    @PostMapping("/drone-table")
    public Result<String> initDroneTable() {
        try {
            JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

            // 创建无人机表
            String createTableSql = """
                CREATE TABLE IF NOT EXISTS drone (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name VARCHAR(100) NOT NULL,
                    model VARCHAR(50),
                    manufacturer VARCHAR(100),
                    weight DECIMAL(10,2),
                    max_altitude DECIMAL(10,2),
                    max_speed DECIMAL(10,2),
                    battery_capacity INTEGER,
                    flight_time INTEGER,
                    camera_resolution VARCHAR(20),
                    status INTEGER DEFAULT 1,
                    create_time DATETIME NOT NULL,
                    update_time DATETIME NOT NULL
                )
                """;
            jdbcTemplate.execute(createTableSql);
            log.info("drone 表创建成功");

            // 先删除旧数据
            jdbcTemplate.execute("DELETE FROM drone");

            // 插入测试数据
            String insertSql = """
                INSERT INTO drone (name, model, manufacturer, weight, max_altitude, max_speed,
                    battery_capacity, flight_time, camera_resolution, status, create_time, update_time)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;
            jdbcTemplate.update(insertSql, "测试无人机1", "T001", "测试厂商A", 1.5, 500, 100, 4000, 30, "4K", 1, LocalDateTime.now(), LocalDateTime.now());
            jdbcTemplate.update(insertSql, "测试无人机2", "T002", "测试厂商B", 2.0, 800, 120, 5000, 40, "4K", 1, LocalDateTime.now(), LocalDateTime.now());
            jdbcTemplate.update(insertSql, "测试无人机3", "T003", "测试厂商C", 1.2, 600, 90, 3500, 25, "1080P", 1, LocalDateTime.now(), LocalDateTime.now());
            jdbcTemplate.update(insertSql, "大疆精灵4", "P4P", "大疆创新", 1.38, 6000, 72, 5870, 30, "4K", 1, LocalDateTime.now(), LocalDateTime.now());
            jdbcTemplate.update(insertSql, "大疆御2", "Mavic2", "大疆创新", 0.91, 5000, 72, 3850, 31, "4K", 1, LocalDateTime.now(), LocalDateTime.now());
            jdbcTemplate.update(insertSql, "大疆经纬M300", "M300", "大疆创新", 6.3, 7000, 80, 9000, 55, "4K", 1, LocalDateTime.now(), LocalDateTime.now());
            jdbcTemplate.update(insertSql, "Parrot Anafi", "Anafi", "Parrot", 0.32, 4000, 50, 2700, 25, "4K", 1, LocalDateTime.now(), LocalDateTime.now());
            jdbcTemplate.update(insertSql, "Autel EVO II", "EVO2", "Autel", 1.9, 6000, 72, 7100, 40, "8K", 1, LocalDateTime.now(), LocalDateTime.now());
            jdbcTemplate.update(insertSql, "飞鲨X1", "X1", "飞鲨科技", 1.1, 4500, 95, 5000, 35, "4K", 1, LocalDateTime.now(), LocalDateTime.now());
            log.info("测试无人机数据插入成功");
            return Result.success("无人机表和9条测试数据创建成功");
        } catch (Exception e) {
            log.error("初始化无人机表失败: {}", e.getMessage());
            return Result.error(500, "初始化失败: " + e.getMessage());
        }
    }
}