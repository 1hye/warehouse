# 无人机信息管理系统 - 设计方案

## 1. 需求分析

### 1.1 功能需求

| 需求编号 | 需求描述 | 来源 |
|---------|---------|------|
| REQ-001 | 无人机信息录入 | 业务功能第1条 |
| REQ-002 | 无人机信息查询 | 业务功能第1条 |
| REQ-003 | 无人机信息修改 | 业务功能第1条 |
| REQ-004 | 无人机信息删除 | 业务功能第1条 |
| REQ-005 | SQLite/MySQL数据库切换 | 技术栈第2条 |
| REQ-006 | 拦截器打印请求信息 | 技术栈第5条 |

### 1.2 非功能需求

| 类别 | 要求 | 来源 |
|------|------|------|
| 数据库 | 支持SQLite（初期）和MySQL | 技术栈第2条 |
| 架构 | 前后端完全分离 | 技术栈第5条 |
| 代码组织 | Service和Mapper接口与实现分离 | 技术栈第4条 |
| 安全 | 使用Apache Shiro进行身份认证 | 技术栈第2条 |

### 1.3 技术约束

| 约束项 | 值 |
|--------|------|
| Java版本 | Java EE 8 |
| Spring Boot版本 | 2.2.x |
| MyBatis版本 | 3.5.x |
| Shiro版本 | 1.7 |
| 前端框架 | Vue 3 + Bootstrap 4 |

---

## 2. 技术方案

### 2.1 架构设计

#### 2.1.1 整体架构

```
┌─────────────────────────────────────────────────────────────────┐
│                        前端层 (Vue 3)                           │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐         │
│  │ 无人机列表页 │  │ 无人机详情页 │  │ 无人机表单页 │         │
│  └──────┬───────┘  └──────┬───────┘  └──────┬───────┘         │
│         │                 │                 │                   │
└─────────┼─────────────────┼─────────────────┼───────────────────┘
          │                 │                 │
          ▼                 ▼                 ▼
┌─────────────────────────────────────────────────────────────────┐
│                        后端层 (Spring Boot)                     │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │                    API Gateway                           │  │
│  │  ┌─────────────┐  ┌─────────────┐  ┌─────────────────┐  │  │
│  │  │  Interceptor│  │   Filter    │  │    Shiro       │  │  │
│  │  │ (请求日志)  │  │ (请求过滤)  │  │ (权限控制)     │  │  │
│  │  └──────┬──────┘  └──────┬──────┘  └───────┬────────┘  │  │
│  │         │                │                  │            │  │
│  │         ▼                ▼                  ▼            │  │
│  │  ┌────────────────────────────────────────────────────┐  │  │
│  │  │              Controller Layer                      │  │  │
│  │  │       DroneController (REST API)                  │  │  │
│  │  └────────────────────┬──────────────────────────────┘  │  │
│  │                       │                                 │  │
│  │                       ▼                                 │  │
│  │  ┌────────────────────────────────────────────────────┐  │  │
│  │  │              Service Layer                         │  │  │
│  │  │   DroneService (接口) → DroneServiceImpl (实现)   │  │  │
│  │  └────────────────────┬──────────────────────────────┘  │  │
│  │                       │                                 │  │
│  │                       ▼                                 │  │
│  │  ┌────────────────────────────────────────────────────┐  │  │
│  │  │              Mapper Layer                          │  │  │
│  │  │        DroneMapper (接口) + XML配置                 │  │  │
│  │  └────────────────────┬──────────────────────────────┘  │  │
│  └───────────────────────┼────────────────────────────────┘  │
│                          │                                   │
│                          ▼                                   │
└───────────────────────────┼───────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────────────┐
│                       数据层                                   │
│  ┌─────────────┐                    ┌─────────────┐          │
│  │   SQLite    │ ←── 配置切换 ──→   │   MySQL     │          │
│  │ (开发/测试) │                    │  (生产)     │          │
│  └─────────────┘                    └─────────────┘          │
└─────────────────────────────────────────────────────────────────┘
```

#### 2.1.2 分层架构约束

| 层级 | 包路径 | 职责 | 允许依赖 |
|------|--------|------|----------|
| Controller | `com.md.basePlatform.controller` | REST API入口，参数校验 | Service层 |
| Service | `com.md.basePlatform.service` | 业务逻辑接口 | Domain、Mapper |
| Service Impl | `com.md.basePlatform.service.impl` | 业务逻辑实现 | Domain、Mapper |
| Mapper | `com.md.basePlatform.mapper` | 数据访问接口 | Domain |
| Domain | `com.md.basePlatform.domain` | 实体类（纯POJO） | 无 |
| DTO | `com.md.basePlatform.dto` | 数据传输对象 | 无 |
| Config | `com.md.basePlatform.config` | 配置类 | 所有层 |
| Interceptor | `com.md.basePlatform.interceptor` | 请求拦截器 | 无 |
| Exception | `com.md.basePlatform.exception` | 异常处理 | 无 |
| Common | `com.md.basePlatform.common` | 通用工具 | 无 |

---

## 3. 数据库设计

### 3.1 数据库表设计

#### 3.1.1 无人机表（drone）

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | INTEGER/BIGINT | PRIMARY KEY, AUTO_INCREMENT | 主键ID |
| name | VARCHAR(100) | NOT NULL | 无人机名称 |
| model | VARCHAR(50) | NOT NULL | 型号 |
| manufacturer | VARCHAR(100) | NULL | 制造商 |
| weight | DECIMAL(10,2) | CHECK(weight > 0) | 重量(kg) |
| max_altitude | INTEGER | DEFAULT 0 | 最大飞行高度(m) |
| max_speed | INTEGER | DEFAULT 0 | 最大速度(km/h) |
| battery_capacity | INTEGER | CHECK(battery_capacity > 0) | 电池容量(mAh) |
| flight_time | INTEGER | CHECK(flight_time > 0) | 续航时间(min) |
| camera_resolution | VARCHAR(50) | NULL | 摄像头分辨率 |
| status | INTEGER | DEFAULT 1 | 状态(0-停用,1-启用) |
| create_time | DATETIME | DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| update_time | DATETIME | DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP | 更新时间 |

#### 3.1.2 DDL语句

**SQLite版本：**
```sql
CREATE TABLE drone (
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

CREATE INDEX idx_drone_name ON drone(name);
CREATE INDEX idx_drone_status ON drone(status);
```

**MySQL版本：**
```sql
CREATE TABLE drone (
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
```

---

## 4. API接口设计

### 4.1 接口总览

| API路径 | HTTP方法 | Controller文件 | 功能描述 |
|---------|----------|----------------|----------|
| /api/drone | GET | DroneController.java | 查询无人机列表（支持分页和条件查询） |
| /api/drone/{id} | GET | DroneController.java | 查询单个无人机详情 |
| /api/drone | POST | DroneController.java | 新增无人机 |
| /api/drone/{id} | PUT | DroneController.java | 更新无人机信息 |
| /api/drone/{id} | DELETE | DroneController.java | 删除无人机 |

### 4.2 接口详细设计

#### 4.2.1 查询无人机列表

**请求：**
```
GET /api/drone?page=1&size=10&name=xxx&status=1
```

**请求参数：**

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| page | Integer | 否 | 页码，默认1 |
| size | Integer | 否 | 每页数量，默认10 |
| name | String | 否 | 无人机名称（模糊查询） |
| model | String | 否 | 型号（模糊查询） |
| manufacturer | String | 否 | 制造商（模糊查询） |
| status | Integer | 否 | 状态筛选 |

**成功响应 (200 OK)：**
```json
{
    "code": 200,
    "message": "success",
    "data": {
        "list": [
            {
                "id": 1,
                "name": "大疆Mavic 3",
                "model": "Mavic 3",
                "manufacturer": "大疆创新",
                "weight": 0.895,
                "maxAltitude": 6000,
                "maxSpeed": 72,
                "batteryCapacity": 5000,
                "flightTime": 46,
                "cameraResolution": "5.1K",
                "status": 1,
                "createTime": "2024-01-15 10:30:00",
                "updateTime": "2024-01-15 10:30:00"
            }
        ],
        "pageNum": 1,
        "pageSize": 10,
        "total": 50,
        "pages": 5
    }
}
```

#### 4.2.2 查询单个无人机

**请求：**
```
GET /api/drone/{id}
```

**路径参数：**

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | Long | 是 | 无人机ID |

**成功响应 (200 OK)：**
```json
{
    "code": 200,
    "message": "success",
    "data": {
        "id": 1,
        "name": "大疆Mavic 3",
        "model": "Mavic 3",
        "manufacturer": "大疆创新",
        "weight": 0.895,
        "maxAltitude": 6000,
        "maxSpeed": 72,
        "batteryCapacity": 5000,
        "flightTime": 46,
        "cameraResolution": "5.1K",
        "status": 1,
        "createTime": "2024-01-15 10:30:00",
        "updateTime": "2024-01-15 10:30:00"
    }
}
```

**失败响应 (404 Not Found)：**
```json
{
    "code": 404,
    "message": "无人机不存在",
    "data": null
}
```

#### 4.2.3 新增无人机

**请求：**
```
POST /api/drone
Content-Type: application/json
```

**请求体：**
```json
{
    "name": "大疆Mavic 3",
    "model": "Mavic 3",
    "manufacturer": "大疆创新",
    "weight": 0.895,
    "maxAltitude": 6000,
    "maxSpeed": 72,
    "batteryCapacity": 5000,
    "flightTime": 46,
    "cameraResolution": "5.1K",
    "status": 1
}
```

**请求体字段说明：**

| 字段名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| name | String | 是 | 无人机名称（最大100字符） |
| model | String | 是 | 型号（最大50字符） |
| manufacturer | String | 否 | 制造商（最大100字符） |
| weight | Double | 是 | 重量(kg)，必须大于0 |
| maxAltitude | Integer | 否 | 最大飞行高度(m)，默认0 |
| maxSpeed | Integer | 否 | 最大速度(km/h)，默认0 |
| batteryCapacity | Integer | 是 | 电池容量(mAh)，必须大于0 |
| flightTime | Integer | 是 | 续航时间(min)，必须大于0 |
| cameraResolution | String | 否 | 摄像头分辨率（最大50字符） |
| status | Integer | 否 | 状态，默认1（启用） |

**成功响应 (201 Created)：**
```json
{
    "code": 201,
    "message": "创建成功",
    "data": {
        "id": 1
    }
}
```

**失败响应 (400 Bad Request)：**
```json
{
    "code": 400,
    "message": "参数校验失败",
    "data": {
        "name": "无人机名称不能为空",
        "weight": "重量必须大于0"
    }
}
```

#### 4.2.4 更新无人机

**请求：**
```
PUT /api/drone/{id}
Content-Type: application/json
```

**请求体：** 同新增接口（所有字段均为可选，只更新传入的字段）

**成功响应 (200 OK)：**
```json
{
    "code": 200,
    "message": "更新成功",
    "data": null
}
```

#### 4.2.5 删除无人机

**请求：**
```
DELETE /api/drone/{id}
```

**成功响应 (200 OK)：**
```json
{
    "code": 200,
    "message": "删除成功",
    "data": null
}
```

### 4.3 错误响应格式

所有接口的错误响应统一格式：

```json
{
    "code": 错误码,
    "message": "错误信息",
    "data": null 或详细错误信息
}
```

**错误码说明：**

| 错误码 | 含义 |
|--------|------|
| 400 | 请求参数错误 |
| 401 | 未认证 |
| 403 | 无权限 |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |

---

## 5. 安全设计

### 5.1 Shiro配置

#### 5.1.1 安全过滤器链

| 路径 | 过滤器 | 说明 |
|------|--------|------|
| /api/** | authcBasic | API接口需要认证 |
| /api/public/** | anon | 公开接口，无需认证 |

#### 5.1.2 权限控制

采用基于角色的访问控制（RBAC）：

| 角色 | 权限 |
|------|------|
| admin | 全部权限（增删改查） |
| user | 查询权限 |

### 5.2 拦截器设计

#### 5.2.1 请求日志拦截器

**类名：** `RequestLogInterceptor.java`

**职责：**
- 记录请求方法、URL、参数
- 记录请求开始和结束时间
- 计算请求耗时
- 记录响应状态码

#### 5.2.2 拦截器配置

在 `WebMvcConfig.java` 中注册拦截器：

```java
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new RequestLogInterceptor())
                .addPathPatterns("/api/**");
    }
}
```

---

## 6. 部署方案

### 6.1 开发环境

**数据库：** SQLite（嵌入式，无需额外安装）

**启动方式：**
```bash
cd baseplatform
mvn spring-boot:run
```

### 6.2 生产环境

**数据库：** MySQL

**配置切换：**
- 修改 `application.yml` 中的数据库配置
- 设置 `spring.profiles.active=mysql`

**打包部署：**
```bash
cd baseplatform
mvn clean package
java -jar target/drone-management-1.0.0.jar --spring.profiles.active=mysql
```

---

## 7. 目录结构

```
backend/                              # Maven后端应用根目录
  ├── src/
  │   └── main/
  │       ├── java/
  │       │   └── com/
  │       │       └── md/
  │       │           └── basePlatform/
  │       │               ├── controller/     # REST API控制层
  │       │               │   └── DroneController.java
  │       │               ├── service/        # 业务逻辑层接口
  │       │               │   └── DroneService.java
  │       │               ├── service/impl/   # 业务逻辑层实现
  │       │               │   └── DroneServiceImpl.java
  │       │               ├── mapper/         # 数据访问层接口
  │       │               │   └── DroneMapper.java
  │       │               ├── domain/         # 实体类
  │       │               │   └── Drone.java
  │       │               ├── dto/            # 数据传输对象
  │       │               │   ├── DroneDTO.java
  │       │               │   └── DroneQuery.java
  │       │               ├── config/         # 配置类
  │       │               │   ├── MyBatisConfig.java
  │       │               │   ├── ShiroConfig.java
  │       │               │   └── WebMvcConfig.java
  │       │               ├── interceptor/    # 拦截器
  │       │               │   └── RequestLogInterceptor.java
  │       │               ├── exception/      # 异常处理
  │       │               │   ├── BusinessException.java
  │       │               │   └── GlobalExceptionHandler.java
  │       │               ├── common/         # 通用工具
  │       │               │   └── Result.java
  │       │               └── BasePlatformApplication.java  # 启动类
  │       └── resources/
  │           ├── application.yml            # 应用配置
  │           ├── application-sqlite.yml     # SQLite配置
  │           ├── application-mysql.yml      # MySQL配置
  │           └── mapper/                    # MyBatis XML配置
  │               └── DroneMapper.xml
  └── pom.xml                                # Maven依赖管理

frontend/                                 # Vue前端应用
  ├── src/
  │   ├── components/                      # 组件
  │   ├── views/                           # 页面视图
  │   ├── api/                             # API调用
  │   ├── utils/                           # 工具函数
  │   └── App.vue
  ├── public/
  ├── package.json
  └── vite.config.js

harness-collab/                           # 协作文档
  ├── 01-product-specs/                   # 需求文档
  ├── 02-design-docs/                     # 设计文档
  ├── 03-exec-plans/                      # 执行计划
  ├── 04-api-docs/                        # API文档
  └── 05-methodology/                     # 方法论文档
```

---

## 8. 关键类设计

### 8.1 Controller层

**DroneController.java**

| 方法名 | 功能 | 参数 | 返回值 |
|--------|------|------|--------|
| list | 查询无人机列表 | DroneQuery query, Pageable pageable | ResponseEntity\<Result\<PageInfo\<DroneDTO\>\>\> |
| getById | 查询单个无人机 | Long id | ResponseEntity\<Result\<DroneDTO\>\> |
| create | 新增无人机 | @Valid DroneDTO dto | ResponseEntity\<Result\<Long\>\> |
| update | 更新无人机 | Long id, @Valid DroneDTO dto | ResponseEntity\<Result\<Void\>\> |
| delete | 删除无人机 | Long id | ResponseEntity\<Result\<Void\>\> |

### 8.2 Service层

**DroneService.java（接口）**

| 方法名 | 功能 | 参数 | 返回值 |
|--------|------|------|--------|
| findAll | 分页查询 | DroneQuery query, Pageable pageable | PageInfo\<DroneDTO\> |
| findById | 根据ID查询 | Long id | DroneDTO |
| create | 创建无人机 | DroneDTO dto | Long（新创建的ID） |
| update | 更新无人机 | Long id, DroneDTO dto | void |
| delete | 删除无人机 | Long id | void |

### 8.3 Mapper层

**DroneMapper.java（接口）**

| 方法名 | 功能 | 参数 | 返回值 |
|--------|------|------|--------|
| selectList | 条件查询列表 | DroneQuery query | List\<Drone\> |
| selectById | 根据ID查询 | Long id | Drone |
| insert | 插入记录 | Drone drone | int |
| updateById | 更新记录 | Drone drone | int |
| deleteById | 删除记录 | Long id | int |
| count | 统计数量 | DroneQuery query | Long |

### 8.4 DTO类

**DroneDTO.java**

| 字段名 | 类型 | 含义 | 约束 |
|--------|------|------|------|
| id | Long | 主键ID | 更新时必填 |
| name | String | 无人机名称 | 必填，最大100字符 |
| model | String | 型号 | 必填，最大50字符 |
| manufacturer | String | 制造商 | 最大100字符 |
| weight | Double | 重量(kg) | 必填，>0 |
| maxAltitude | Integer | 最大飞行高度(m) | >=0 |
| maxSpeed | Integer | 最大速度(km/h) | >=0 |
| batteryCapacity | Integer | 电池容量(mAh) | 必填，>0 |
| flightTime | Integer | 续航时间(min) | 必填，>0 |
| cameraResolution | String | 摄像头分辨率 | 最大50字符 |
| status | Integer | 状态 | 0或1，默认1 |

**DroneQuery.java**

| 字段名 | 类型 | 含义 |
|--------|------|------|
| name | String | 无人机名称（模糊查询） |
| model | String | 型号（模糊查询） |
| manufacturer | String | 制造商（模糊查询） |
| status | Integer | 状态筛选 |

---

## 9. 配置文件设计

### 9.1 application.yml（主配置）

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

### 9.2 application-sqlite.yml（SQLite配置）

```yaml
spring:
  datasource:
    url: jdbc:sqlite:example_db.db
    driver-class-name: org.sqlite.JDBC

pagehelper:
  helper-dialect: sqlite
```

### 9.3 application-mysql.yml（MySQL配置）

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

## 10. 异常处理设计

### 10.1 自定义异常

**BusinessException.java**

| 字段名 | 类型 | 含义 |
|--------|------|------|
| code | Integer | 错误码 |
| message | String | 错误信息 |

### 10.2 全局异常处理器

**GlobalExceptionHandler.java**

| 处理的异常类型 | HTTP状态码 | 处理逻辑 |
|----------------|-----------|----------|
| BusinessException | 400/404等 | 直接返回异常信息 |
| MethodArgumentNotValidException | 400 | 提取校验错误信息 |
| HttpMessageNotReadableException | 400 | 返回请求体格式错误 |
| Exception | 500 | 返回服务器内部错误 |

---

## 11. 前端设计概要

### 11.1 页面结构

| 页面 | 路径 | 功能 |
|------|------|------|
| 列表页 | /drone | 展示无人机列表，支持搜索和分页 |
| 详情页 | /drone/{id} | 展示单个无人机详细信息 |
| 新增页 | /drone/create | 新增无人机表单 |
| 编辑页 | /drone/{id}/edit | 编辑无人机表单 |

### 11.2 API调用模块

**api/drone.js**

| 方法名 | 功能 | 参数 |
|--------|------|------|
| getList | 获取无人机列表 | params（分页和查询条件） |
| getById | 获取单个无人机 | id |
| create | 创建无人机 | data |
| update | 更新无人机 | id, data |
| delete | 删除无人机 | id |

---

## 12. 交付物清单

| 序号 | 交付物 | 状态 | 备注 |
|------|--------|------|------|
| 1 | 需求文档 | 待创建 | harness-collab/01-product-specs/ |
| 2 | 设计文档 | 已完成 | 本文件 |
| 3 | Controller层代码 | 待开发 | controller/DroneController.java |
| 4 | Service层代码 | 待开发 | service/DroneService.java, service/impl/DroneServiceImpl.java |
| 5 | Mapper层代码 | 待开发 | mapper/DroneMapper.java, resources/mapper/DroneMapper.xml |
| 6 | Domain层代码 | 待开发 | domain/Drone.java |
| 7 | DTO层代码 | 待开发 | dto/DroneDTO.java, dto/DroneQuery.java |
| 8 | 配置类 | 待开发 | config/* |
| 9 | 拦截器 | 待开发 | interceptor/RequestLogInterceptor.java |
| 10 | 异常处理 | 待开发 | exception/* |
| 11 | 通用工具 | 待开发 | common/Result.java |
| 12 | 测试代码 | 待开发 | src/test/* |
| 13 | API文档 | 待更新 | harness-collab/04-api-docs/ |
| 14 | 功能清单 | 待更新 | harness-collab/func.md |