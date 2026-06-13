---
name: drone-management-skill
description: 无人机信息管理系统开发技能。用于快速生成符合项目规范的Controller、Service、Mapper和Domain代码，支持CRUD操作。
license: MIT
---

# 无人机信息管理系统开发技能

## 项目技术栈

| 层级 | 技术 | 版本 |
|------|------|------|
| 框架 | Spring Boot | 3.2.5 |
| 核心 | Spring Framework | 6.1.6 |
| Servlet | Jakarta Servlet | 6.0 (Tomcat 10.1) |
| 安全 | Apache Shiro | 1.13.0 (Jakarta) |
| ORM | MyBatis | 3.5.x (spring-boot-starter 3.0.3) |
| 验证 | Hibernate Validation | 8.x (由 Spring Boot 3.2.5 管理) |
| 连接池 | Alibaba Druid | 1.2.23 |
| 数据库 | SQLite / MySQL | - |
| 分页 | PageHelper | 1.4.1 |
| API文档 | SpringDoc OpenAPI | 2.5.0 |
| 编译 | Java | 21 |
| 构建 | Maven | 3.x |
| 前端 | Vue | 3.5.35 |
| 前端UI | Bootstrap | 4.6.2 |

## 代码结构规范

### 包结构

```
com.md.basePlatform/
├── controller/     # REST API控制层
├── service/        # 业务逻辑层（接口）
├── service/impl/   # 业务逻辑层（实现）
├── mapper/         # 数据访问层（接口）
├── domain/         # 实体类
├── dto/            # 数据传输对象
├── config/         # 配置类（含shiro子包）
├── common/         # 通用工具/统一响应
├── exception/      # 异常处理
└── interceptor/    # 拦截器
```

### 命名规范

- **Controller**: `{Entity}Controller.java`
- **Service接口**: `{Entity}Service.java`
- **Service实现**: `{Entity}ServiceImpl.java`
- **Mapper**: `{Entity}Mapper.java`
- **Domain**: `{Entity}.java`
- **DTO**: `{Entity}DTO.java`

## 无人机实体属性

根据需求，无人机属性由AI自动生成：

| 属性名 | 类型 | 说明 | 约束 |
|--------|------|------|------|
| id | Long | 主键ID | 自增 |
| name | String | 无人机名称 | 非空，最大100字符 |
| model | String | 型号 | 非空，最大50字符 |
| manufacturer | String | 制造商 | 最大100字符 |
| weight | Double | 重量(kg) | 大于0 |
| maxAltitude | Integer | 最大飞行高度(m) | 非负整数 |
| maxSpeed | Integer | 最大速度(km/h) | 非负整数 |
| batteryCapacity | Integer | 电池容量(mAh) | 正整数 |
| flightTime | Integer | 续航时间(min) | 正整数 |
| cameraResolution | String | 摄像头分辨率 | 最大50字符 |
| status | Integer | 状态(0-停用,1-启用) | 默认1 |
| createTime | LocalDateTime | 创建时间 | 自动生成 |
| updateTime | LocalDateTime | 更新时间 | 自动更新 |

## CRUD操作规范

### Controller层

```java
@Slf4j
@RestController
@RequestMapping("/api/drone")
@RequiredArgsConstructor
@Validated
public class DroneController {

    private final DroneService droneService;

    @GetMapping
    public ResponseEntity<Result<PageInfo<DroneDTO>>> list(
            @ModelAttribute DroneQuery query,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize);

    @GetMapping("/{id}")
    public ResponseEntity<Result<DroneDTO>> getById(@PathVariable Long id);

    @PostMapping
    public ResponseEntity<Result<DroneDTO>> create(@Valid @RequestBody DroneDTO dto);

    @PutMapping("/{id}")
    public ResponseEntity<Result<DroneDTO>> update(
            @PathVariable Long id, @Valid @RequestBody DroneDTO dto);

    @DeleteMapping("/{id}")
    public ResponseEntity<Result<Void>> delete(@PathVariable Long id);
}
```

所有 Controller 使用 `ResponseEntity<Result<T>>` 统一响应格式，日志使用 `@Slf4j`。

### Service层

- 接口和实现分离，接口放在`service`包，实现放在`service/impl`包
- 使用构造器注入（Lombok `@RequiredArgsConstructor`）
- 所有公共方法必须有Javadoc注释

### Mapper层

- 使用XML配置（resources/mapper/目录）
- 支持SQLite和MySQL双数据源切换
- 分页查询使用PageHelper

## 数据库支持

### SQLite配置（默认）

适用于开发和测试环境，无需额外安装数据库。配置在 `application-sqlite.yml`。

### MySQL配置

适用于生产环境，配置在 `application-mysql.yml`。
切换方式：修改 `application.yml` 中的 `spring.profiles.active` 为 `mysql`。

## 拦截器规范

- 拦截器放在 `interceptor` 包
- 执行时打印请求信息（请求方法、URL、参数、执行时间）
- 通过 `WebMvcConfig` 注册拦截器

## 安全框架

使用 Apache Shiro 1.13.0 (Jakarta版) 进行身份认证和权限管理：
- 配置 Shiro 过滤器链（`ShiroConfig.java`）
- 实现自定义 Realm（`CustomRealm.java`）— MD5 密码加密
- URL 拦截规则：
  - `/api/auth/login` — 匿名访问
  - `/api/auth/register` — 匿名访问
  - `/api/**` — 需登录认证

## 异常处理

- 全局异常处理器统一处理异常（`GlobalExceptionHandler.java`）
- 自定义业务异常类（`BusinessException.java`）
- 返回统一的 `Result` 错误响应格式

## 验证规则

使用 Hibernate Validation（Jakarta Bean Validation）进行参数校验：
- `@NotBlank` — 非空字符串
- `@NotNull` — 非空对象
- `@Min/@Max` — 数值范围
- `@Size` — 字符串长度
- Controller 类上使用 `@Validated`，参数上使用 `@Valid`

## 前后端交互规范

- 前后端完全分离，通过 RESTful API 交换数据
- 统一响应格式：

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {}
}
```

- 前端 Vue 3 + Bootstrap 4，使用 Axios 调用后端 API

## 交付标准

1. **代码规范**: 遵循Java编码规范
2. **测试覆盖率**: 单元测试覆盖率≥80%
3. **文档同步**: API文档和func.md同步更新
4. **CI验证**: Maven构建通过