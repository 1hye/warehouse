# 无人机信息管理系统 - 实施方案

## 1. 文档信息

| 属性 | 值 |
|------|------|
| 版本 | 1.0.0 |
| 创建日期 | 2024-01-15 |
| 最后更新 | 2024-01-15 |
| 作者 | 研发团队 |

---

## 2. 实施步骤

### 2.1 步骤概览

| 步骤 | 阶段 | 内容 | 参考文档 |
|------|------|------|----------|
| Step 1 | 环境搭建 | 创建Spring Boot项目，配置pom.xml | 设计文档第9章 |
| Step 2 | 配置文件 | 创建application.yml及数据库配置 | 设计文档第9章 |
| Step 3 | 通用组件 | 创建Result、异常处理类 | 设计文档第10章 |
| Step 4 | Domain层 | 创建Drone实体类 | 设计文档第3章 |
| Step 5 | DTO层 | 创建DroneDTO、DroneQuery | 设计文档第8章 |
| Step 6 | Mapper层 | 创建DroneMapper接口和XML | 设计文档第8章 |
| Step 7 | Service层 | 创建DroneService接口和实现 | 设计文档第8章 |
| Step 8 | Controller层 | 创建DroneController | 设计文档第8章 |
| Step 9 | 拦截器 | 创建RequestLogInterceptor | 设计文档第5章 |
| Step 10 | 配置类 | 创建WebMvcConfig、MyBatisConfig、ShiroConfig | 设计文档第5章 |
| Step 11 | 测试 | 编写单元测试 | 执行计划第4章 |

---

## 3. 代码实现

### 3.1 Step 1：环境搭建

**pom.xml 关键依赖：**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>2.2.13.RELEASE</version>
        <relativePath/>
    </parent>
    
    <groupId>com.md</groupId>
    <artifactId>drone-management</artifactId>
    <version>1.0.0</version>
    <name>drone-management</name>
    <description>无人机信息管理系统</description>
    
    <properties>
        <java.version>1.8</java.version>
        <mybatis-spring.version>2.0.6</mybatis-spring.version>
        <shiro.version>1.7.1</shiro.version>
        <pagehelper.version>1.4.1</pagehelper.version>
    </properties>
    
    <dependencies>
        <!-- Spring Boot Starter Web -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        
        <!-- Spring Boot Starter Validation -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-validation</artifactId>
        </dependency>
        
        <!-- MyBatis -->
        <dependency>
            <groupId>org.mybatis.spring.boot</groupId>
            <artifactId>mybatis-spring-boot-starter</artifactId>
            <version>2.1.4</version>
        </dependency>
        
        <!-- SQLite -->
        <dependency>
            <groupId>org.xerial</groupId>
            <artifactId>sqlite-jdbc</artifactId>
            <version>3.36.0.3</version>
        </dependency>
        
        <!-- MySQL -->
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <scope>runtime</scope>
        </dependency>
        
        <!-- Druid -->
        <dependency>
            <groupId>com.alibaba</groupId>
            <artifactId>druid-spring-boot-starter</artifactId>
            <version>1.2.8</version>
        </dependency>
        
        <!-- Shiro -->
        <dependency>
            <groupId>org.apache.shiro</groupId>
            <artifactId>shiro-spring-boot-web-starter</artifactId>
            <version>${shiro.version}</version>
        </dependency>
        
        <!-- PageHelper -->
        <dependency>
            <groupId>com.github.pagehelper</groupId>
            <artifactId>pagehelper-spring-boot-starter</artifactId>
            <version>${pagehelper.version}</version>
        </dependency>
        
        <!-- Lombok -->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>
        
        <!-- Test -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
        
        <dependency>
            <groupId>org.mybatis.spring.boot</groupId>
            <artifactId>mybatis-spring-boot-starter-test</artifactId>
            <version>2.1.4</version>
            <scope>test</scope>
        </dependency>
    </dependencies>
    
    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
                <configuration>
                    <excludes>
                        <exclude>
                            <groupId>org.projectlombok</groupId>
                            <artifactId>lombok</artifactId>
                        </exclude>
                    </excludes>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
```

---

### 3.2 Step 2：配置文件

**application.yml：**

```yaml
server:
  port: 8080

spring:
  profiles:
    active: sqlite
  application:
    name: drone-management

mybatis:
  mapper-locations: classpath:mapper/*.xml
  type-aliases-package: com.md.basePlatform.domain

pagehelper:
  helper-dialect: sqlite
  reasonable: true
  support-methods-arguments: true
  params: count=countSql

logging:
  level:
    com.md.basePlatform: DEBUG
    com.md.basePlatform.mapper: DEBUG
```

**application-sqlite.yml：**

```yaml
spring:
  datasource:
    url: jdbc:sqlite:example_db.db
    driver-class-name: org.sqlite.JDBC

pagehelper:
  helper-dialect: sqlite
```

**application-mysql.yml：**

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/example_db?useSSL=false&serverTimezone=UTC&characterEncoding=utf8mb4
    username: admin
    password: password
    driver-class-name: com.mysql.cj.jdbc.Driver
    type: com.alibaba.druid.pool.DruidDataSource

pagehelper:
  helper-dialect: mysql
```

---

### 3.3 Step 3：通用组件

**Result.java（统一响应封装）：**

```java
package com.md.basePlatform.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Result<T> {
    
    /**
     * 业务状态码
     */
    private Integer code;
    
    /**
     * 响应消息
     */
    private String message;
    
    /**
     * 响应数据
     */
    private T data;
    
    /**
     * 成功响应
     */
    public static <T> Result<T> success(T data) {
        return Result.<T>builder()
                .code(200)
                .message("success")
                .data(data)
                .build();
    }
    
    /**
     * 成功响应（无数据）
     */
    public static <T> Result<T> success() {
        return success(null);
    }
    
    /**
     * 创建成功响应
     */
    public static <T> Result<T> created(T data) {
        return Result.<T>builder()
                .code(201)
                .message("创建成功")
                .data(data)
                .build();
    }
    
    /**
     * 失败响应
     */
    public static <T> Result<T> error(Integer code, String message) {
        return Result.<T>builder()
                .code(code)
                .message(message)
                .data(null)
                .build();
    }
    
    /**
     * 参数错误响应
     */
    public static <T> Result<T> badRequest(String message) {
        return error(400, message);
    }
    
    /**
     * 未找到响应
     */
    public static <T> Result<T> notFound(String message) {
        return error(404, message);
    }
    
    /**
     * 服务器错误响应
     */
    public static <T> Result<T> serverError(String message) {
        return error(500, message);
    }
}
```

**BusinessException.java（业务异常）：**

```java
package com.md.basePlatform.exception;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {
    
    private final Integer code;
    
    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
    }
    
    public BusinessException(String message) {
        this(400, message);
    }
    
    public static BusinessException notFound(String message) {
        return new BusinessException(404, message);
    }
}
```

**GlobalExceptionHandler.java（全局异常处理器）：**

```java
package com.md.basePlatform.exception;

import com.md.basePlatform.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    /**
     * 处理业务异常
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Result<Void>> handleBusinessException(BusinessException e) {
        log.warn("业务异常: {}", e.getMessage());
        return ResponseEntity
                .status(e.getCode() >= 500 ? HttpStatus.INTERNAL_SERVER_ERROR : HttpStatus.BAD_REQUEST)
                .body(Result.error(e.getCode(), e.getMessage()));
    }
    
    /**
     * 处理参数校验异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Result<Map<String, String>>> handleValidationException(
            MethodArgumentNotValidException e) {
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        log.warn("参数校验失败: {}", errors);
        return ResponseEntity.badRequest().body(Result.<Map<String, String>>builder()
                .code(400)
                .message("参数校验失败")
                .data(errors)
                .build());
    }
    
    /**
     * 处理其他异常
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Result<Void>> handleException(Exception e) {
        log.error("服务器内部错误", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Result.serverError("服务器内部错误"));
    }
}
```

---

### 3.4 Step 4：Domain层

**Drone.java（实体类）：**

```java
package com.md.basePlatform.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Drone {
    
    /**
     * 主键ID
     */
    private Long id;
    
    /**
     * 无人机名称
     */
    private String name;
    
    /**
     * 型号
     */
    private String model;
    
    /**
     * 制造商
     */
    private String manufacturer;
    
    /**
     * 重量(kg)
     */
    private Double weight;
    
    /**
     * 最大飞行高度(m)
     */
    private Integer maxAltitude;
    
    /**
     * 最大速度(km/h)
     */
    private Integer maxSpeed;
    
    /**
     * 电池容量(mAh)
     */
    private Integer batteryCapacity;
    
    /**
     * 续航时间(min)
     */
    private Integer flightTime;
    
    /**
     * 摄像头分辨率
     */
    private String cameraResolution;
    
    /**
     * 状态(0-停用,1-启用)
     */
    private Integer status;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
```

---

### 3.5 Step 5：DTO层

**DroneDTO.java（数据传输对象）：**

```java
package com.md.basePlatform.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DroneDTO {
    
    /**
     * 主键ID（更新时使用）
     */
    private Long id;
    
    /**
     * 无人机名称
     */
    @NotBlank(message = "无人机名称不能为空")
    @Size(max = 100, message = "无人机名称最大100字符")
    private String name;
    
    /**
     * 型号
     */
    @NotBlank(message = "型号不能为空")
    @Size(max = 50, message = "型号最大50字符")
    private String model;
    
    /**
     * 制造商
     */
    @Size(max = 100, message = "制造商最大100字符")
    private String manufacturer;
    
    /**
     * 重量(kg)
     */
    @NotNull(message = "重量不能为空")
    @Positive(message = "重量必须大于0")
    private Double weight;
    
    /**
     * 最大飞行高度(m)
     */
    @Min(value = 0, message = "最大飞行高度不能为负数")
    private Integer maxAltitude;
    
    /**
     * 最大速度(km/h)
     */
    @Min(value = 0, message = "最大速度不能为负数")
    private Integer maxSpeed;
    
    /**
     * 电池容量(mAh)
     */
    @NotNull(message = "电池容量不能为空")
    @Positive(message = "电池容量必须大于0")
    private Integer batteryCapacity;
    
    /**
     * 续航时间(min)
     */
    @NotNull(message = "续航时间不能为空")
    @Positive(message = "续航时间必须大于0")
    private Integer flightTime;
    
    /**
     * 摄像头分辨率
     */
    @Size(max = 50, message = "摄像头分辨率最大50字符")
    private String cameraResolution;
    
    /**
     * 状态(0-停用,1-启用)
     */
    @Min(value = 0, message = "状态值无效")
    @Max(value = 1, message = "状态值无效")
    private Integer status;
    
    /**
     * 创建时间
     */
    private String createTime;
    
    /**
     * 更新时间
     */
    private String updateTime;
}
```

**DroneQuery.java（查询条件）：**

```java
package com.md.basePlatform.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DroneQuery {
    
    /**
     * 无人机名称（模糊查询）
     */
    private String name;
    
    /**
     * 型号（模糊查询）
     */
    private String model;
    
    /**
     * 制造商（模糊查询）
     */
    private String manufacturer;
    
    /**
     * 状态筛选
     */
    private Integer status;
}
```

---

### 3.6 Step 6：Mapper层

**DroneMapper.java（接口）：**

```java
package com.md.basePlatform.mapper;

import com.md.basePlatform.domain.Drone;
import com.md.basePlatform.dto.DroneQuery;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DroneMapper {
    
    /**
     * 条件查询列表
     */
    List<Drone> selectList(DroneQuery query);
    
    /**
     * 根据ID查询
     */
    Drone selectById(@Param("id") Long id);
    
    /**
     * 插入记录
     */
    int insert(Drone drone);
    
    /**
     * 更新记录
     */
    int updateById(Drone drone);
    
    /**
     * 删除记录
     */
    int deleteById(@Param("id") Long id);
    
    /**
     * 统计数量
     */
    Long count(DroneQuery query);
}
```

**DroneMapper.xml（MyBatis配置）：**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" 
"http://mybatis.org/dtd/mybatis-3-mapper.dtd">

<mapper namespace="com.md.basePlatform.mapper.DroneMapper">
    
    <resultMap id="BaseResultMap" type="com.md.basePlatform.domain.Drone">
        <id column="id" property="id"/>
        <result column="name" property="name"/>
        <result column="model" property="model"/>
        <result column="manufacturer" property="manufacturer"/>
        <result column="weight" property="weight"/>
        <result column="max_altitude" property="maxAltitude"/>
        <result column="max_speed" property="maxSpeed"/>
        <result column="battery_capacity" property="batteryCapacity"/>
        <result column="flight_time" property="flightTime"/>
        <result column="camera_resolution" property="cameraResolution"/>
        <result column="status" property="status"/>
        <result column="create_time" property="createTime"/>
        <result column="update_time" property="updateTime"/>
    </resultMap>
    
    <sql id="BaseColumnList">
        id, name, model, manufacturer, weight, max_altitude, max_speed, 
        battery_capacity, flight_time, camera_resolution, status, create_time, update_time
    </sql>
    
    <select id="selectList" resultMap="BaseResultMap">
        SELECT <include refid="BaseColumnList"/>
        FROM drone
        <where>
            <if test="name != null and name != ''">
                AND name LIKE CONCAT('%', #{name}, '%')
            </if>
            <if test="model != null and model != ''">
                AND model LIKE CONCAT('%', #{model}, '%')
            </if>
            <if test="manufacturer != null and manufacturer != ''">
                AND manufacturer LIKE CONCAT('%', #{manufacturer}, '%')
            </if>
            <if test="status != null">
                AND status = #{status}
            </if>
        </where>
        ORDER BY create_time DESC
    </select>
    
    <select id="selectById" resultMap="BaseResultMap">
        SELECT <include refid="BaseColumnList"/>
        FROM drone
        WHERE id = #{id}
    </select>
    
    <insert id="insert" parameterType="com.md.basePlatform.domain.Drone" useGeneratedKeys="true" keyProperty="id">
        INSERT INTO drone (name, model, manufacturer, weight, max_altitude, max_speed, 
            battery_capacity, flight_time, camera_resolution, status, create_time, update_time)
        VALUES (#{name}, #{model}, #{manufacturer}, #{weight}, #{maxAltitude}, #{maxSpeed}, 
            #{batteryCapacity}, #{flightTime}, #{cameraResolution}, #{status}, 
            #{createTime}, #{updateTime})
    </insert>
    
    <update id="updateById" parameterType="com.md.basePlatform.domain.Drone">
        UPDATE drone
        SET 
            <if test="name != null">name = #{name},</if>
            <if test="model != null">model = #{model},</if>
            <if test="manufacturer != null">manufacturer = #{manufacturer},</if>
            <if test="weight != null">weight = #{weight},</if>
            <if test="maxAltitude != null">max_altitude = #{maxAltitude},</if>
            <if test="maxSpeed != null">max_speed = #{maxSpeed},</if>
            <if test="batteryCapacity != null">battery_capacity = #{batteryCapacity},</if>
            <if test="flightTime != null">flight_time = #{flightTime},</if>
            <if test="cameraResolution != null">camera_resolution = #{cameraResolution},</if>
            <if test="status != null">status = #{status},</if>
            update_time = #{updateTime}
        WHERE id = #{id}
    </update>
    
    <delete id="deleteById">
        DELETE FROM drone WHERE id = #{id}
    </delete>
    
    <select id="count" resultType="java.lang.Long">
        SELECT COUNT(*) FROM drone
        <where>
            <if test="name != null and name != ''">
                AND name LIKE CONCAT('%', #{name}, '%')
            </if>
            <if test="model != null and model != ''">
                AND model LIKE CONCAT('%', #{model}, '%')
            </if>
            <if test="manufacturer != null and manufacturer != ''">
                AND manufacturer LIKE CONCAT('%', #{manufacturer}, '%')
            </if>
            <if test="status != null">
                AND status = #{status}
            </if>
        </where>
    </select>
</mapper>
```

---

### 3.7 Step 7：Service层

**DroneService.java（接口）：**

```java
package com.md.basePlatform.service;

import com.md.basePlatform.dto.DroneDTO;
import com.md.basePlatform.dto.DroneQuery;
import com.github.pagehelper.PageInfo;

/**
 * 无人机服务接口
 */
public interface DroneService {
    
    /**
     * 分页查询无人机列表
     *
     * @param query 查询条件
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 分页结果
     */
    PageInfo<DroneDTO> findAll(DroneQuery query, Integer pageNum, Integer pageSize);
    
    /**
     * 根据ID查询无人机
     *
     * @param id 无人机ID
     * @return 无人机DTO
     */
    DroneDTO findById(Long id);
    
    /**
     * 创建无人机
     *
     * @param dto 无人机DTO
     * @return 新创建的ID
     */
    Long create(DroneDTO dto);
    
    /**
     * 更新无人机
     *
     * @param id 无人机ID
     * @param dto 无人机DTO
     */
    void update(Long id, DroneDTO dto);
    
    /**
     * 删除无人机
     *
     * @param id 无人机ID
     */
    void delete(Long id);
}
```

**DroneServiceImpl.java（实现类）：**

```java
package com.md.basePlatform.service.impl;

import com.md.basePlatform.domain.Drone;
import com.md.basePlatform.dto.DroneDTO;
import com.md.basePlatform.dto.DroneQuery;
import com.md.basePlatform.exception.BusinessException;
import com.md.basePlatform.mapper.DroneMapper;
import com.md.basePlatform.service.DroneService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DroneServiceImpl implements DroneService {
    
    private final DroneMapper droneMapper;
    
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    @Override
    public PageInfo<DroneDTO> findAll(DroneQuery query, Integer pageNum, Integer pageSize) {
        log.info("查询无人机列表: query={}, pageNum={}, pageSize={}", query, pageNum, pageSize);
        
        PageHelper.startPage(pageNum, pageSize);
        List<Drone> drones = droneMapper.selectList(query);
        
        List<DroneDTO> dtoList = drones.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        return new PageInfo<>(dtoList);
    }
    
    @Override
    public DroneDTO findById(Long id) {
        log.info("查询无人机详情: id={}", id);
        
        Drone drone = droneMapper.selectById(id);
        if (drone == null) {
            throw BusinessException.notFound("无人机不存在");
        }
        
        return convertToDTO(drone);
    }
    
    @Override
    @Transactional
    public Long create(DroneDTO dto) {
        log.info("创建无人机: dto={}", dto);
        
        Drone drone = convertToDomain(dto);
        drone.setStatus(dto.getStatus() != null ? dto.getStatus() : 1);
        drone.setCreateTime(LocalDateTime.now());
        drone.setUpdateTime(LocalDateTime.now());
        
        droneMapper.insert(drone);
        log.info("无人机创建成功: id={}", drone.getId());
        
        return drone.getId();
    }
    
    @Override
    @Transactional
    public void update(Long id, DroneDTO dto) {
        log.info("更新无人机: id={}, dto={}", id, dto);
        
        Drone existing = droneMapper.selectById(id);
        if (existing == null) {
            throw BusinessException.notFound("无人机不存在");
        }
        
        Drone drone = convertToDomain(dto);
        drone.setId(id);
        drone.setUpdateTime(LocalDateTime.now());
        
        droneMapper.updateById(drone);
        log.info("无人机更新成功: id={}", id);
    }
    
    @Override
    @Transactional
    public void delete(Long id) {
        log.info("删除无人机: id={}", id);
        
        Drone drone = droneMapper.selectById(id);
        if (drone == null) {
            throw BusinessException.notFound("无人机不存在");
        }
        
        droneMapper.deleteById(id);
        log.info("无人机删除成功: id={}", id);
    }
    
    /**
     * Domain转DTO
     */
    private DroneDTO convertToDTO(Drone drone) {
        return DroneDTO.builder()
                .id(drone.getId())
                .name(drone.getName())
                .model(drone.getModel())
                .manufacturer(drone.getManufacturer())
                .weight(drone.getWeight())
                .maxAltitude(drone.getMaxAltitude())
                .maxSpeed(drone.getMaxSpeed())
                .batteryCapacity(drone.getBatteryCapacity())
                .flightTime(drone.getFlightTime())
                .cameraResolution(drone.getCameraResolution())
                .status(drone.getStatus())
                .createTime(drone.getCreateTime() != null ? 
                        drone.getCreateTime().format(FORMATTER) : null)
                .updateTime(drone.getUpdateTime() != null ? 
                        drone.getUpdateTime().format(FORMATTER) : null)
                .build();
    }
    
    /**
     * DTO转Domain
     */
    private Drone convertToDomain(DroneDTO dto) {
        return Drone.builder()
                .id(dto.getId())
                .name(dto.getName())
                .model(dto.getModel())
                .manufacturer(dto.getManufacturer())
                .weight(dto.getWeight())
                .maxAltitude(dto.getMaxAltitude())
                .maxSpeed(dto.getMaxSpeed())
                .batteryCapacity(dto.getBatteryCapacity())
                .flightTime(dto.getFlightTime())
                .cameraResolution(dto.getCameraResolution())
                .status(dto.getStatus())
                .build();
    }
}
```

---

### 3.8 Step 8：Controller层

**DroneController.java：**

```java
package com.md.basePlatform.controller;

import com.md.basePlatform.common.Result;
import com.md.basePlatform.dto.DroneDTO;
import com.md.basePlatform.dto.DroneQuery;
import com.md.basePlatform.service.DroneService;
import com.github.pagehelper.PageInfo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/drone")
@Tag(name = "无人机管理", description = "无人机信息CRUD接口")
@RequiredArgsConstructor
public class DroneController {
    
    private final DroneService droneService;
    
    /**
     * 查询无人机列表
     */
    @GetMapping
    @Operation(summary = "查询无人机列表", description = "支持分页和条件查询")
    public ResponseEntity<Result<PageInfo<DroneDTO>>> list(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "无人机名称") @RequestParam(required = false) String name,
            @Parameter(description = "型号") @RequestParam(required = false) String model,
            @Parameter(description = "制造商") @RequestParam(required = false) String manufacturer,
            @Parameter(description = "状态") @RequestParam(required = false) Integer status) {
        
        log.info("查询无人机列表: page={}, size={}, name={}, model={}, manufacturer={}, status={}", 
                page, size, name, model, manufacturer, status);
        
        DroneQuery query = DroneQuery.builder()
                .name(name)
                .model(model)
                .manufacturer(manufacturer)
                .status(status)
                .build();
        
        PageInfo<DroneDTO> result = droneService.findAll(query, page, size);
        return ResponseEntity.ok(Result.success(result));
    }
    
    /**
     * 查询单个无人机
     */
    @GetMapping("/{id}")
    @Operation(summary = "查询无人机详情", description = "根据ID查询无人机详细信息")
    public ResponseEntity<Result<DroneDTO>> getById(
            @Parameter(description = "无人机ID") @PathVariable Long id) {
        
        log.info("查询无人机详情: id={}", id);
        
        DroneDTO result = droneService.findById(id);
        return ResponseEntity.ok(Result.success(result));
    }
    
    /**
     * 新增无人机
     */
    @PostMapping
    @Operation(summary = "新增无人机", description = "创建新的无人机记录")
    public ResponseEntity<Result<Map<String, Long>>> create(
            @Validated @RequestBody DroneDTO dto) {
        
        log.info("新增无人机: dto={}", dto);
        
        Long id = droneService.create(dto);
        Map<String, Long> result = new HashMap<>();
        result.put("id", id);
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Result.created(result));
    }
    
    /**
     * 更新无人机
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新无人机", description = "更新指定ID的无人机信息")
    public ResponseEntity<Result<Void>> update(
            @Parameter(description = "无人机ID") @PathVariable Long id,
            @Validated @RequestBody DroneDTO dto) {
        
        log.info("更新无人机: id={}, dto={}", id, dto);
        
        droneService.update(id, dto);
        return ResponseEntity.ok(Result.success());
    }
    
    /**
     * 删除无人机
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除无人机", description = "删除指定ID的无人机")
    public ResponseEntity<Result<Void>> delete(
            @Parameter(description = "无人机ID") @PathVariable Long id) {
        
        log.info("删除无人机: id={}", id);
        
        droneService.delete(id);
        return ResponseEntity.ok(Result.success());
    }
}
```

---

### 3.9 Step 9：拦截器

**RequestLogInterceptor.java：**

```java
package com.md.basePlatform.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Enumeration;

@Slf4j
public class RequestLogInterceptor implements HandlerInterceptor {
    
    private static final ThreadLocal<Long> startTimeThreadLocal = new ThreadLocal<>();
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, 
                            Object handler) throws Exception {
        
        // 记录请求开始时间
        startTimeThreadLocal.set(System.currentTimeMillis());
        
        // 打印请求信息
        StringBuilder params = new StringBuilder();
        Enumeration<String> paramNames = request.getParameterNames();
        while (paramNames.hasMoreElements()) {
            String paramName = paramNames.nextElement();
            params.append(paramName).append("=").append(request.getParameter(paramName)).append("&");
        }
        
        log.info("====================================== Request Start ======================================");
        log.info("请求方法: {}", request.getMethod());
        log.info("请求URL: {}", request.getRequestURL());
        log.info("请求参数: {}", params.length() > 0 ? params.substring(0, params.length() - 1) : "无");
        log.info("请求来源: {}", request.getRemoteAddr());
        log.info("请求时间: {}", System.currentTimeMillis());
        log.info("-------------------------------------------------------------------------------------------");
        
        return true;
    }
    
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, 
                          Object handler, ModelAndView modelAndView) throws Exception {
        // 可以在这里添加响应处理逻辑
    }
    
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, 
                                Object handler, Exception ex) throws Exception {
        
        // 计算请求耗时
        long startTime = startTimeThreadLocal.get();
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        
        // 打印响应信息
        log.info("-------------------------------------------------------------------------------------------");
        log.info("响应状态码: {}", response.getStatus());
        log.info("请求耗时: {}ms", duration);
        log.info("====================================== Request End ======================================");
        
        // 清理ThreadLocal
        startTimeThreadLocal.remove();
    }
}
```

---

### 3.10 Step 10：配置类

**WebMvcConfig.java：**

```java
package com.md.basePlatform.config;

import com.md.basePlatform.interceptor.RequestLogInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new RequestLogInterceptor())
                .addPathPatterns("/api/**");
    }
}
```

**MyBatisConfig.java：**

```java
package com.md.basePlatform.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("com.md.basePlatform.mapper")
public class MyBatisConfig {
    // MyBatis配置
}
```

**ShiroConfig.java：**

```java
package com.md.basePlatform.config;

import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.LinkedHashMap;
import java.util.Map;

@Configuration
public class ShiroConfig {
    
    /**
     * 安全管理器
     */
    @Bean
    public DefaultWebSecurityManager securityManager() {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        // 配置Realm等
        return securityManager;
    }
    
    /**
     * Shiro过滤器
     */
    @Bean
    public ShiroFilterFactoryBean shiroFilter(DefaultWebSecurityManager securityManager) {
        ShiroFilterFactoryBean filterFactoryBean = new ShiroFilterFactoryBean();
        filterFactoryBean.setSecurityManager(securityManager);
        
        // 设置登录URL（如果需要）
        // filterFactoryBean.setLoginUrl("/api/login");
        
        // 配置拦截规则
        Map<String, String> filterChainDefinitionMap = new LinkedHashMap<>();
        
        // 公开接口，不需要认证
        filterChainDefinitionMap.put("/api/public/**", "anon");
        
        // API接口需要认证
        filterChainDefinitionMap.put("/api/**", "authcBasic");
        
        filterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);
        
        return filterFactoryBean;
    }
}
```

---

### 3.11 Step 11：启动类

**BasePlatformApplication.java：**

```java
package com.md.basePlatform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 无人机信息管理系统启动类
 */
@SpringBootApplication
public class BasePlatformApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(BasePlatformApplication.class, args);
    }
}
```

---

## 4. 数据库初始化

### 4.1 SQLite数据库初始化

创建SQLite数据库文件和表：

```sql
-- 创建无人机表
CREATE TABLE IF NOT EXISTS drone (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name VARCHAR(100) NOT NULL,
    model VARCHAR(50) NOT NULL,
    manufacturer VARCHAR(100),
    weight DECIMAL(10,2) CHECK(weight > 0),
    max_altitude INTEGER DEFAULT 0,
    max_speed INTEGER DEFAULT 0,
    battery_capacity INTEGER CHECK(battery_capacity > 0),
    flight_time INTEGER CHECK(flight_time > 0),
    camera_resolution VARCHAR(50),
    status INTEGER DEFAULT 1,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 创建索引
CREATE INDEX IF NOT EXISTS idx_drone_name ON drone(name);
CREATE INDEX IF NOT EXISTS idx_drone_status ON drone(status);

-- 插入测试数据
INSERT INTO drone (name, model, manufacturer, weight, max_altitude, max_speed, 
    battery_capacity, flight_time, camera_resolution, status) VALUES
('大疆Mavic 3', 'Mavic 3', '大疆创新', 0.895, 6000, 72, 5000, 46, '5.1K', 1),
('大疆Air 2S', 'Air 2S', '大疆创新', 0.595, 5000, 68, 3500, 31, '5.4K', 1),
('大疆Mini 3 Pro', 'Mini 3 Pro', '大疆创新', 0.249, 4000, 57, 2490, 34, '4K', 1);
```

### 4.2 MySQL数据库初始化

```sql
-- 创建数据库（如果不存在）
CREATE DATABASE IF NOT EXISTS example_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE example_db;

-- 创建无人机表
CREATE TABLE IF NOT EXISTS drone (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    model VARCHAR(50) NOT NULL,
    manufacturer VARCHAR(100),
    weight DECIMAL(10,2) CHECK(weight > 0),
    max_altitude INT DEFAULT 0,
    max_speed INT DEFAULT 0,
    battery_capacity INT CHECK(battery_capacity > 0),
    flight_time INT CHECK(flight_time > 0),
    camera_resolution VARCHAR(50),
    status INT DEFAULT 1,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_drone_name (name),
    INDEX idx_drone_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 插入测试数据
INSERT INTO drone (name, model, manufacturer, weight, max_altitude, max_speed, 
    battery_capacity, flight_time, camera_resolution, status) VALUES
('大疆Mavic 3', 'Mavic 3', '大疆创新', 0.895, 6000, 72, 5000, 46, '5.1K', 1),
('大疆Air 2S', 'Air 2S', '大疆创新', 0.595, 5000, 68, 3500, 31, '5.4K', 1),
('大疆Mini 3 Pro', 'Mini 3 Pro', '大疆创新', 0.249, 4000, 57, 2490, 34, '4K', 1);
```

---

## 5. 启动与验证

### 5.1 启动应用

```bash
# 使用SQLite配置启动
cd baseplatform
mvn spring-boot:run

# 或使用MySQL配置启动
mvn spring-boot:run -Dspring.profiles.active=mysql
```

### 5.2 验证接口

```bash
# 查询无人机列表
curl http://localhost:8080/api/drone

# 查询单个无人机
curl http://localhost:8080/api/drone/1

# 新增无人机
curl -X POST http://localhost:8080/api/drone \
  -H "Content-Type: application/json" \
  -d '{
    "name": "测试无人机",
    "model": "Test Model",
    "manufacturer": "测试厂商",
    "weight": 1.0,
    "maxAltitude": 5000,
    "maxSpeed": 60,
    "batteryCapacity": 4000,
    "flightTime": 30,
    "cameraResolution": "4K",
    "status": 1
  }'

# 更新无人机
curl -X PUT http://localhost:8080/api/drone/4 \
  -H "Content-Type: application/json" \
  -d '{
    "name": "测试无人机-更新",
    "model": "Test Model"
  }'

# 删除无人机
curl -X DELETE http://localhost:8080/api/drone/4
```

---

## 6. 测试覆盖

### 6.1 单元测试示例

**DroneServiceTest.java：**

```java
package com.md.basePlatform.service;

import com.md.basePlatform.dto.DroneDTO;
import com.md.basePlatform.dto.DroneQuery;
import com.md.basePlatform.exception.BusinessException;
import com.github.pagehelper.PageInfo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class DroneServiceTest {
    
    @Autowired
    private DroneService droneService;
    
    @Test
    void testCreateAndFindById() {
        // 创建
        DroneDTO dto = DroneDTO.builder()
                .name("Test Drone")
                .model("Test Model")
                .weight(1.0)
                .batteryCapacity(4000)
                .flightTime(30)
                .build();
        
        Long id = droneService.create(dto);
        assertNotNull(id);
        
        // 查询
        DroneDTO result = droneService.findById(id);
        assertNotNull(result);
        assertEquals("Test Drone", result.getName());
        assertEquals("Test Model", result.getModel());
    }
    
    @Test
    void testFindByIdNotFound() {
        assertThrows(BusinessException.class, () -> {
            droneService.findById(99999L);
        });
    }
    
    @Test
    void testUpdate() {
        // 先创建
        DroneDTO dto = DroneDTO.builder()
                .name("Original")
                .model("Model")
                .weight(1.0)
                .batteryCapacity(4000)
                .flightTime(30)
                .build();
        
        Long id = droneService.create(dto);
        
        // 更新
        DroneDTO updateDto = DroneDTO.builder()
                .name("Updated")
                .build();
        
        droneService.update(id, updateDto);
        
        // 验证
        DroneDTO result = droneService.findById(id);
        assertEquals("Updated", result.getName());
        assertEquals("Model", result.getModel()); // 未更新的字段保持不变
    }
    
    @Test
    void testDelete() {
        // 先创建
        DroneDTO dto = DroneDTO.builder()
                .name("To Delete")
                .model("Model")
                .weight(1.0)
                .batteryCapacity(4000)
                .flightTime(30)
                .build();
        
        Long id = droneService.create(dto);
        
        // 删除
        droneService.delete(id);
        
        // 验证不存在
        assertThrows(BusinessException.class, () -> {
            droneService.findById(id);
        });
    }
    
    @Test
    void testFindAll() {
        DroneQuery query = new DroneQuery();
        PageInfo<DroneDTO> result = droneService.findAll(query, 1, 10);
        
        assertNotNull(result);
        assertNotNull(result.getList());
        assertTrue(result.getTotal() >= 0);
    }
}
```

---

## 7. 交付清单

| 序号 | 交付物 | 状态 | 说明 |
|------|--------|------|------|
| 1 | pom.xml | ✅ | Maven依赖配置 |
| 2 | application.yml | ✅ | 主配置文件 |
| 3 | application-sqlite.yml | ✅ | SQLite配置 |
| 4 | application-mysql.yml | ✅ | MySQL配置 |
| 5 | BasePlatformApplication.java | ✅ | 启动类 |
| 6 | Drone.java | ✅ | 实体类 |
| 7 | DroneDTO.java | ✅ | 数据传输对象 |
| 8 | DroneQuery.java | ✅ | 查询条件对象 |
| 9 | DroneMapper.java | ✅ | Mapper接口 |
| 10 | DroneMapper.xml | ✅ | MyBatis配置 |
| 11 | DroneService.java | ✅ | Service接口 |
| 12 | DroneServiceImpl.java | ✅ | Service实现 |
| 13 | DroneController.java | ✅ | Controller |
| 14 | RequestLogInterceptor.java | ✅ | 请求日志拦截器 |
| 15 | WebMvcConfig.java | ✅ | Web配置 |
| 16 | MyBatisConfig.java | ✅ | MyBatis配置 |
| 17 | ShiroConfig.java | ✅ | Shiro配置 |
| 18 | Result.java | ✅ | 统一响应封装 |
| 19 | BusinessException.java | ✅ | 业务异常 |
| 20 | GlobalExceptionHandler.java | ✅ | 全局异常处理器 |
| 21 | DroneServiceTest.java | ✅ | 单元测试 |