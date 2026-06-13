package com.md.basePlatform.mapper;

import com.md.basePlatform.domain.Drone;
import com.md.basePlatform.dto.DroneQuery;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 无人机数据访问层接口
 * 定义无人机数据库操作方法
 */
@Mapper
public interface DroneMapper {
    
    /**
     * 查询无人机列表（支持条件查询）
     * @param query 查询条件
     * @return 无人机列表
     */
    List<Drone> selectList(DroneQuery query);
    
    /**
     * 根据ID查询无人机
     * @param id 无人机ID
     * @return 无人机对象
     */
    Drone selectById(@Param("id") Long id);
    
    /**
     * 插入无人机
     * @param drone 无人机对象
     * @return 影响行数
     */
    int insert(Drone drone);
    
    /**
     * 根据ID更新无人机
     * @param drone 无人机对象
     * @return 影响行数
     */
    int updateById(Drone drone);
    
    /**
     * 根据ID删除无人机
     * @param id 无人机ID
     * @return 影响行数
     */
    int deleteById(@Param("id") Long id);
    
    /**
     * 统计无人机数量
     * @param query 查询条件
     * @return 数量
     */
    Long count(DroneQuery query);
}