package com.md.basePlatform.service;

import com.md.basePlatform.dto.DroneDTO;
import com.md.basePlatform.dto.DroneQuery;
import com.github.pagehelper.PageInfo;

/**
 * 无人机服务接口
 * 定义无人机业务逻辑操作
 */
public interface DroneService {
    
    /**
     * 查询无人机列表（分页+条件查询）
     * @param query 查询条件（包含关键词搜索）
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 分页后的无人机列表
     */
    PageInfo<DroneDTO> findAll(DroneQuery query, Integer pageNum, Integer pageSize);
    
    /**
     * 根据ID查询无人机详情
     * @param id 无人机ID
     * @return 无人机详细信息
     */
    DroneDTO findById(Long id);
    
    /**
     * 创建新无人机
     * @param dto 无人机信息
     * @return 创建成功的无人机ID
     */
    Long create(DroneDTO dto);
    
    /**
     * 更新无人机信息
     * @param id 无人机ID
     * @param dto 新的无人机信息
     */
    void update(Long id, DroneDTO dto);
    
    /**
     * 删除无人机
     * @param id 无人机ID
     */
    void delete(Long id);
}