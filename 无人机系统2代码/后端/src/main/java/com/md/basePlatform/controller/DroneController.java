package com.md.basePlatform.controller;

import com.md.basePlatform.common.Result;
import com.md.basePlatform.dto.DroneDTO;
import com.md.basePlatform.dto.DroneQuery;
import com.md.basePlatform.service.DroneService;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

/**
 * 无人机管理控制器
 * 提供无人机信息的增删改查RESTful API接口
 * 路径: /api/drone
 */
@Slf4j
@RestController
@RequestMapping("/api/drone")
@RequiredArgsConstructor
@Validated
public class DroneController {
    
    private final DroneService droneService;
    
    /**
     * 查询无人机列表（分页+条件查询）
     * @param query 查询条件（包含关键词搜索）
     * @param pageNum 页码，默认1
     * @param pageSize 每页数量，默认10
     * @return 分页后的无人机列表
     */
    @GetMapping
    public ResponseEntity<Result<PageInfo<DroneDTO>>> list(
            @ModelAttribute DroneQuery query,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        log.info("查询无人机列表请求: query={}, pageNum={}, pageSize={}", query, pageNum, pageSize);
        
        PageInfo<DroneDTO> result = droneService.findAll(query, pageNum, pageSize);
        return ResponseEntity.ok(Result.success(result));
    }
    
    /**
     * 根据ID查询无人机详情
     * @param id 无人机ID
     * @return 无人机详细信息
     */
    @GetMapping("/{id}")
    public ResponseEntity<Result<DroneDTO>> getById(@PathVariable Long id) {
        log.info("查询无人机详情请求: id={}", id);
        
        DroneDTO result = droneService.findById(id);
        return ResponseEntity.ok(Result.success(result));
    }
    
    /**
     * 创建新无人机
     * @param dto 无人机信息
     * @return 创建成功的无人机信息
     */
    @PostMapping
    public ResponseEntity<Result<DroneDTO>> create(@Valid @RequestBody DroneDTO dto) {
        log.info("创建无人机请求: dto={}", dto);
        
        Long id = droneService.create(dto);
        DroneDTO result = droneService.findById(id);
        return ResponseEntity.status(HttpStatus.CREATED).body(Result.created(result));
    }
    
    /**
     * 更新无人机信息
     * @param id 无人机ID
     * @param dto 新的无人机信息
     * @return 更新后的无人机信息
     */
    @PutMapping("/{id}")
    public ResponseEntity<Result<DroneDTO>> update(
            @PathVariable Long id,
            @Valid @RequestBody DroneDTO dto) {
        log.info("更新无人机请求: id={}, dto={}", id, dto);
        
        droneService.update(id, dto);
        DroneDTO result = droneService.findById(id);
        return ResponseEntity.ok(Result.success(result));
    }
    
    /**
     * 删除无人机
     * @param id 无人机ID
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Result<Void>> delete(@PathVariable Long id) {
        log.info("删除无人机请求: id={}", id);
        
        droneService.delete(id);
        return ResponseEntity.ok(Result.success());
    }
}