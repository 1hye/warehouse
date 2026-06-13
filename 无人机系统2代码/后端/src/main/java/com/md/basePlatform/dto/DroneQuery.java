package com.md.basePlatform.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 无人机查询数据传输对象
 * 用于无人机列表的条件查询
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DroneQuery {

    /**
     * 关键词搜索（同时匹配名称、型号、厂商）
     */
    private String keyword;

    /**
     * 无人机名称（模糊查询）
     */
    private String name;
    
    /**
     * 型号（模糊查询）
     */
    private String model;
    
    /**
     * 生产厂商（模糊查询）
     */
    private String manufacturer;
    
    /**
     * 状态（精确查询）
     */
    private Integer status;
}