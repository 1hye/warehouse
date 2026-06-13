package com.example.uav.domain;

import com.example.uav.common.BaseEntity;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * 无人机实体类
 */
public class Uav extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private Long id;

    /** 无人机名称 */
    @NotBlank(message = "无人机名称不能为空")
    @Size(max = 100, message = "无人机名称长度不能超过100个字符")
    private String name;

    /** 无人机类型 */
    @Size(max = 50, message = "无人机类型长度不能超过50个字符")
    private String type;

    /** 序列号 */
    @Size(max = 100, message = "序列号长度不能超过100个字符")
    private String serialNumber;

    /** 最大飞行时间（分钟） */
    private Integer maxFlightTime;

    /** 最大飞行距离（公里） */
    private Double maxRange;

    /** 最大飞行高度（米） */
    private Double maxAltitude;

    /** 有效载荷（千克） */
    private Double payloadCapacity;

    /** 电池容量（mAh） */
    private Integer batteryCapacity;

    /** 重量（克） */
    private Double weight;

    /** 状态：active-正常 maintenance-维护 retired-退役 */
    private String status = "active";

    /** AI 生成的描述信息 */
    private String description;

    public Uav() {}

    public Uav(String name, String type) {
        this.name = name;
        this.type = type;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public Integer getMaxFlightTime() {
        return maxFlightTime;
    }

    public void setMaxFlightTime(Integer maxFlightTime) {
        this.maxFlightTime = maxFlightTime;
    }

    public Double getMaxRange() {
        return maxRange;
    }

    public void setMaxRange(Double maxRange) {
        this.maxRange = maxRange;
    }

    public Double getMaxAltitude() {
        return maxAltitude;
    }

    public void setMaxAltitude(Double maxAltitude) {
        this.maxAltitude = maxAltitude;
    }

    public Double getPayloadCapacity() {
        return payloadCapacity;
    }

    public void setPayloadCapacity(Double payloadCapacity) {
        this.payloadCapacity = payloadCapacity;
    }

    public Integer getBatteryCapacity() {
        return batteryCapacity;
    }

    public void setBatteryCapacity(Integer batteryCapacity) {
        this.batteryCapacity = batteryCapacity;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
