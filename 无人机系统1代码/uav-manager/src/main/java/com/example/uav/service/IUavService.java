package com.example.uav.service;

import com.example.uav.domain.Uav;
import java.util.List;

/**
 * 无人机服务层接口
 */
public interface IUavService {

    /**
     * 查询无人机列表
     */
    List<Uav> selectUavList(Uav uav);

    /**
     * 根据 ID 查询无人机
     */
    Uav selectUavById(Long id);

    /**
     * 新增无人机
     */
    int insertUav(Uav uav);

    /**
     * 修改无人机
     */
    int updateUav(Uav uav);

    /**
     * 删除无人机
     */
    int deleteUavById(Long id);

    /**
     * AI 自动生成无人机属性
     */
    Uav generateAiAttributes(String name, String type);
}
