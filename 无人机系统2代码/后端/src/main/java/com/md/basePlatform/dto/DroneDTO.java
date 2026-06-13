package com.md.basePlatform.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.*;

/**
 * 无人机数据传输对象
 * 用于前后端之间无人机数据的传输
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DroneDTO {
    
    /**
     * 无人机ID
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
     * 生产厂商
     */
    @Size(max = 100, message = "制造商最大100字符")
    private String manufacturer;
    
    /**
     * 重量（kg）
     */
    @NotNull(message = "重量不能为空")
    @Positive(message = "重量必须大于0")
    private Double weight;
    
    /**
     * 最大飞行高度（m）
     */
    @Min(value = 0, message = "最大飞行高度不能为负数")
    private Integer maxAltitude;
    
    /**
     * 最大飞行速度（km/h）
     */
    @Min(value = 0, message = "最大速度不能为负数")
    private Integer maxSpeed;
    
    /**
     * 电池容量（mAh）
     */
    @NotNull(message = "电池容量不能为空")
    @Positive(message = "电池容量必须大于0")
    private Integer batteryCapacity;
    
    /**
     * 续航时间（分钟）
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
     * 状态（1-启用，0-禁用）
     */
    @Min(value = 0, message = "状态值无效")
    @Max(value = 1, message = "状态值无效")
    private Integer status;
    
    /**
     * 创建时间（字符串格式）
     */
    private String createTime;
    
    /**
     * 更新时间（字符串格式）
     */
    private String updateTime;
}