package com.md.basePlatform.service.impl;

import com.md.basePlatform.domain.Drone;
import com.md.basePlatform.dto.DroneDTO;
import com.md.basePlatform.dto.DroneQuery;
import com.md.basePlatform.exception.BusinessException;
import com.md.basePlatform.mapper.DroneMapper;
import com.md.basePlatform.service.DroneService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 无人机服务实现类
 * 实现无人机业务逻辑处理
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DroneServiceImpl implements DroneService {
    
    private final DroneMapper droneMapper;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * 分页查询无人机列表
     * 支持关键词模糊搜索（名称、型号、厂商）
     * @param query 查询条件
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 分页结果
     */
    @Override
    public PageInfo<DroneDTO> findAll(DroneQuery query, Integer pageNum, Integer pageSize) {
        log.info("查询无人机列表: query={}, pageNum={}, pageSize={}", query, pageNum, pageSize);

        if (query.getKeyword() != null && !query.getKeyword().trim().isEmpty()) {
            query.setName(query.getKeyword());
            query.setModel(query.getKeyword());
            query.setManufacturer(query.getKeyword());
        }

        PageHelper.startPage(pageNum, pageSize);
        List<Drone> drones = droneMapper.selectList(query);

        List<DroneDTO> dtoList = drones.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return new PageInfo<>(dtoList);
    }
    
    /**
     * 根据ID查询无人机详情
     * @param id 无人机ID
     * @return 无人机详情
     * @throws BusinessException 无人机不存在时抛出
     */
    @Override
    public DroneDTO findById(Long id) {
        log.info("查询无人机详情: id={}", id);
        
        Drone drone = droneMapper.selectById(id);
        if (drone == null) {
            throw BusinessException.notFound("无人机不存在");
        }
        
        return convertToDTO(drone);
    }
    
    /**
     * 创建无人机
     * @param dto 无人机信息
     * @return 新创建的无人机ID
     */
    @Override
    @Transactional
    public Long create(DroneDTO dto) {
        log.info("创建无人机: dto={}", dto);
        
        Drone drone = convertToDomain(dto);
        drone.setStatus(dto.getStatus() != null ? dto.getStatus() : 1);
        drone.setCreateTime(LocalDateTime.now());
        drone.setUpdateTime(LocalDateTime.now());
        
        droneMapper.insert(drone);
        log.info("无人机创建成功: id={}", drone.getId());
        
        return drone.getId();
    }
    
    /**
     * 更新无人机信息
     * @param id 无人机ID
     * @param dto 新的无人机信息
     * @throws BusinessException 无人机不存在时抛出
     */
    @Override
    @Transactional
    public void update(Long id, DroneDTO dto) {
        log.info("更新无人机: id={}, dto={}", id, dto);
        
        Drone existing = droneMapper.selectById(id);
        if (existing == null) {
            throw BusinessException.notFound("无人机不存在");
        }
        
        Drone drone = convertToDomain(dto);
        drone.setId(id);
        drone.setUpdateTime(LocalDateTime.now());
        
        droneMapper.updateById(drone);
        log.info("无人机更新成功: id={}", id);
    }
    
    /**
     * 删除无人机
     * @param id 无人机ID
     * @throws BusinessException 无人机不存在时抛出
     */
    @Override
    @Transactional
    public void delete(Long id) {
        log.info("删除无人机: id={}", id);
        
        Drone drone = droneMapper.selectById(id);
        if (drone == null) {
            throw BusinessException.notFound("无人机不存在");
        }
        
        droneMapper.deleteById(id);
        log.info("无人机删除成功: id={}", id);
    }
    
    /**
     * 将Domain实体转换为DTO
     * @param drone Domain实体
     * @return DTO对象
     */
    private DroneDTO convertToDTO(Drone drone) {
        return DroneDTO.builder()
                .id(drone.getId())
                .name(drone.getName())
                .model(drone.getModel())
                .manufacturer(drone.getManufacturer())
                .weight(drone.getWeight())
                .maxAltitude(drone.getMaxAltitude())
                .maxSpeed(drone.getMaxSpeed())
                .batteryCapacity(drone.getBatteryCapacity())
                .flightTime(drone.getFlightTime())
                .cameraResolution(drone.getCameraResolution())
                .status(drone.getStatus())
                .createTime(drone.getCreateTime() != null ? 
                        drone.getCreateTime().format(FORMATTER) : null)
                .updateTime(drone.getUpdateTime() != null ? 
                        drone.getUpdateTime().format(FORMATTER) : null)
                .build();
    }
    
    /**
     * 将DTO转换为Domain实体
     * @param dto DTO对象
     * @return Domain实体
     */
    private Drone convertToDomain(DroneDTO dto) {
        return Drone.builder()
                .id(dto.getId())
                .name(dto.getName())
                .model(dto.getModel())
                .manufacturer(dto.getManufacturer())
                .weight(dto.getWeight())
                .maxAltitude(dto.getMaxAltitude())
                .maxSpeed(dto.getMaxSpeed())
                .batteryCapacity(dto.getBatteryCapacity())
                .flightTime(dto.getFlightTime())
                .cameraResolution(dto.getCameraResolution())
                .status(dto.getStatus())
                .build();
    }
}