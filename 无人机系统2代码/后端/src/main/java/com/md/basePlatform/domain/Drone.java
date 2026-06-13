package com.md.basePlatform.domain;

import lombok.Builder;
import java.time.LocalDateTime;

/**
 * 无人机实体类
 *
 * 对应数据库表：drone
 * 职责：封装从数据库中查询出的原始无人机记录数据，
 *      与 drone 表的字段一一对应。
 *
 * 注意：本实体类用于在 Service 层和 Mapper 层之间传递数据；
 * 前端交互时由 Service 层转换为 DroneDTO 后返回给 Controller。
 */
@Builder
public class Drone {

    /** 无人机唯一标识（主键，自增） */
    private Long id;

    /** 无人机名称，如 "大疆精灵4" */
    private String name;

    /** 无人机型号，如 "P4P" */
    private String model;

    /** 生产厂商，如 "大疆创新" */
    private String manufacturer;

    /** 重量，单位：千克（kg） */
    private Double weight;

    /** 最大飞行高度，单位：米（m） */
    private Integer maxAltitude;

    /** 最大飞行速度，单位：千米/小时（km/h） */
    private Integer maxSpeed;

    /** 电池容量，单位：毫安时（mAh） */
    private Integer batteryCapacity;

    /** 续航时间，单位：分钟 */
    private Integer flightTime;

    /** 摄像头分辨率，如 "4K"、"1080P"、"8K" */
    private String cameraResolution;

    /**
     * 状态
     * 1 — 启用（正常使用中）
     * 0 — 禁用（已停用或报废）
     */
    private Integer status;

    /** 创建时间，插入时自动生成 */
    private LocalDateTime createTime;

    /** 更新时间，每次修改时自动更新 */
    private LocalDateTime updateTime;

    /* ==================== 构造方法 ==================== */

    /** 无参构造方法（MyBatis 需要） */
    public Drone() {}

    /**
     * 全参构造方法
     * @param id               无人机 ID
     * @param name             名称
     * @param model            型号
     * @param manufacturer     厂商
     * @param weight           重量
     * @param maxAltitude      最大飞行高度
     * @param maxSpeed         最大速度
     * @param batteryCapacity  电池容量
     * @param flightTime       续航时间
     * @param cameraResolution 摄像头分辨率
     * @param status           状态
     * @param createTime       创建时间
     * @param updateTime       更新时间
     */
    public Drone(Long id, String name, String model, String manufacturer, Double weight,
                 Integer maxAltitude, Integer maxSpeed, Integer batteryCapacity,
                 Integer flightTime, String cameraResolution, Integer status,
                 LocalDateTime createTime, LocalDateTime updateTime) {
        this.id = id;
        this.name = name;
        this.model = model;
        this.manufacturer = manufacturer;
        this.weight = weight;
        this.maxAltitude = maxAltitude;
        this.maxSpeed = maxSpeed;
        this.batteryCapacity = batteryCapacity;
        this.flightTime = flightTime;
        this.cameraResolution = cameraResolution;
        this.status = status;
        this.createTime = createTime;
        this.updateTime = updateTime;
    }

    /* ==================== Getter / Setter 方法 ==================== */

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public String getManufacturer() { return manufacturer; }
    public void setManufacturer(String manufacturer) { this.manufacturer = manufacturer; }

    public Double getWeight() { return weight; }
    public void setWeight(Double weight) { this.weight = weight; }

    public Integer getMaxAltitude() { return maxAltitude; }
    public void setMaxAltitude(Integer maxAltitude) { this.maxAltitude = maxAltitude; }

    public Integer getMaxSpeed() { return maxSpeed; }
    public void setMaxSpeed(Integer maxSpeed) { this.maxSpeed = maxSpeed; }

    public Integer getBatteryCapacity() { return batteryCapacity; }
    public void setBatteryCapacity(Integer batteryCapacity) { this.batteryCapacity = batteryCapacity; }

    public Integer getFlightTime() { return flightTime; }
    public void setFlightTime(Integer flightTime) { this.flightTime = flightTime; }

    public String getCameraResolution() { return cameraResolution; }
    public void setCameraResolution(String cameraResolution) { this.cameraResolution = cameraResolution; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }

    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }

    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }
}