# 无人机管理 API 文档

## 1. 文档信息

| 属性 | 值 |
|------|------|
| 版本 | 1.0.0 |
| 创建日期 | 2024-01-15 |
| 最后更新 | 2024-01-15 |
| 所属模块 | 无人机管理 |

---

## 2. 基础信息

### 2.1 基础路径

所有接口的基础路径为：`/api/drone`

### 2.2 认证方式

- API接口需要通过Shiro认证
- 支持HTTP Basic Authentication

### 2.3 响应格式

所有接口的响应格式统一为JSON：

```json
{
    "code": 200,
    "message": "success",
    "data": {}
}
```

**响应字段说明：**

| 字段名 | 类型 | 说明 |
|--------|------|------|
| code | Integer | 业务状态码，200表示成功 |
| message | String | 响应消息 |
| data | Object | 响应数据，可为null |

### 2.4 错误码说明

| 错误码 | HTTP状态码 | 含义 |
|--------|-----------|------|
| 200 | 200 | 成功 |
| 400 | 400 | 请求参数错误 |
| 401 | 401 | 未认证 |
| 403 | 403 | 无权限 |
| 404 | 404 | 资源不存在 |
| 500 | 500 | 服务器内部错误 |

---

## 3. 接口列表

| API路径 | HTTP方法 | 功能描述 | 所属文件 |
|---------|----------|----------|----------|
| /api/drone | GET | 查询无人机列表（分页） | DroneController.java |
| /api/drone/{id} | GET | 查询单个无人机详情 | DroneController.java |
| /api/drone | POST | 新增无人机 | DroneController.java |
| /api/drone/{id} | PUT | 更新无人机信息 | DroneController.java |
| /api/drone/{id} | DELETE | 删除无人机 | DroneController.java |

---

## 4. 接口详细说明

### 4.1 查询无人机列表

**接口路径：** `GET /api/drone`

**功能描述：** 查询无人机列表，支持分页和条件查询

**请求参数（Query Parameters）：**

| 参数名 | 类型 | 必填 | 默认值 | 说明 |
|--------|------|------|--------|------|
| page | Integer | 否 | 1 | 页码 |
| size | Integer | 否 | 10 | 每页数量 |
| name | String | 否 | - | 无人机名称（模糊查询） |
| model | String | 否 | - | 型号（模糊查询） |
| manufacturer | String | 否 | - | 制造商（模糊查询） |
| status | Integer | 否 | - | 状态筛选（0-停用，1-启用） |

**请求示例：**
```
GET /api/drone?page=1&size=10&name=Mavic&status=1
```

**成功响应（200 OK）：**
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

**响应数据结构：**

| 字段名 | 类型 | 说明 |
|--------|------|------|
| list | Array | 无人机列表数据 |
| list[].id | Long | 无人机ID |
| list[].name | String | 无人机名称 |
| list[].model | String | 型号 |
| list[].manufacturer | String | 制造商 |
| list[].weight | Double | 重量(kg) |
| list[].maxAltitude | Integer | 最大飞行高度(m) |
| list[].maxSpeed | Integer | 最大速度(km/h) |
| list[].batteryCapacity | Integer | 电池容量(mAh) |
| list[].flightTime | Integer | 续航时间(min) |
| list[].cameraResolution | String | 摄像头分辨率 |
| list[].status | Integer | 状态（0-停用，1-启用） |
| list[].createTime | String | 创建时间 |
| list[].updateTime | String | 更新时间 |
| pageNum | Integer | 当前页码 |
| pageSize | Integer | 每页数量 |
| total | Long | 总记录数 |
| pages | Integer | 总页数 |

---

### 4.2 查询单个无人机

**接口路径：** `GET /api/drone/{id}`

**功能描述：** 根据ID查询单个无人机详情

**路径参数：**

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | Long | 是 | 无人机ID |

**请求示例：**
```
GET /api/drone/1
```

**成功响应（200 OK）：**
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

**失败响应（404 Not Found）：**
```json
{
    "code": 404,
    "message": "无人机不存在",
    "data": null
}
```

---

### 4.3 新增无人机

**接口路径：** `POST /api/drone`

**功能描述：** 创建新的无人机记录

**请求头：**
```
Content-Type: application/json
```

**请求体：**

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

**请求示例：**
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

**成功响应（201 Created）：**
```json
{
    "code": 201,
    "message": "创建成功",
    "data": {
        "id": 1
    }
}
```

**失败响应（400 Bad Request）：**
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

---

### 4.4 更新无人机

**接口路径：** `PUT /api/drone/{id}`

**功能描述：** 更新指定ID的无人机信息

**路径参数：**

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | Long | 是 | 无人机ID |

**请求头：**
```
Content-Type: application/json
```

**请求体：** 同新增接口，但所有字段均为可选，只更新传入的字段

**请求示例：**
```json
{
    "name": "大疆Mavic 3 Pro",
    "weight": 0.95
}
```

**成功响应（200 OK）：**
```json
{
    "code": 200,
    "message": "更新成功",
    "data": null
}
```

**失败响应（404 Not Found）：**
```json
{
    "code": 404,
    "message": "无人机不存在",
    "data": null
}
```

---

### 4.5 删除无人机

**接口路径：** `DELETE /api/drone/{id}`

**功能描述：** 删除指定ID的无人机

**路径参数：**

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | Long | 是 | 无人机ID |

**请求示例：**
```
DELETE /api/drone/1
```

**成功响应（200 OK）：**
```json
{
    "code": 200,
    "message": "删除成功",
    "data": null
}
```

**失败响应（404 Not Found）：**
```json
{
    "code": 404,
    "message": "无人机不存在",
    "data": null
}
```

---

## 5. 数据模型

### 5.1 DroneDTO（无人机数据传输对象）

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
| createTime | String | 创建时间 | 只读 |
| updateTime | String | 更新时间 | 只读 |

### 5.2 DroneQuery（查询条件对象）

| 字段名 | 类型 | 含义 |
|--------|------|------|
| name | String | 无人机名称（模糊查询） |
| model | String | 型号（模糊查询） |
| manufacturer | String | 制造商（模糊查询） |
| status | Integer | 状态筛选 |

---

## 6. 权限说明

| 接口 | 管理员权限 | 普通用户权限 |
|------|-----------|-------------|
| GET /api/drone | ✅ | ✅ |
| GET /api/drone/{id} | ✅ | ✅ |
| POST /api/drone | ✅ | ❌ |
| PUT /api/drone/{id} | ✅ | ❌ |
| DELETE /api/drone/{id} | ✅ | ❌ |

---

## 7. 版本历史

| 版本 | 日期 | 变更说明 |
|------|------|---------|
| 1.0.0 | 2024-01-15 | 初始版本 |