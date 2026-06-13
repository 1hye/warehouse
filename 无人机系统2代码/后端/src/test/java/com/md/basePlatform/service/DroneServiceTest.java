package com.md.basePlatform.service;

import com.md.basePlatform.dto.DroneDTO;
import com.md.basePlatform.dto.DroneQuery;
import com.md.basePlatform.exception.BusinessException;
import com.github.pagehelper.PageInfo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback
class DroneServiceTest {
    
    @Autowired
    private DroneService droneService;
    
    @Test
    void testFindAll() {
        PageInfo<DroneDTO> result = droneService.findAll(new DroneQuery(), 1, 10);
        assertNotNull(result);
        assertTrue(result.getTotal() >= 0);
    }
    
    @Test
    void testFindById_Success() {
        DroneDTO dto = DroneDTO.builder()
                .name("TestDrone")
                .model("T001")
                .weight(1.0)
                .batteryCapacity(4000)
                .flightTime(30)
                .build();
        
        Long id = droneService.create(dto);
        
        DroneDTO result = droneService.findById(id);
        assertNotNull(result);
        assertEquals("TestDrone", result.getName());
    }
    
    @Test
    void testFindById_NotFound() {
        assertThrows(BusinessException.class, () -> droneService.findById(9999L));
    }
    
    @Test
    void testCreate() {
        DroneDTO dto = DroneDTO.builder()
                .name("TestCreate")
                .model("T002")
                .manufacturer("TestManufacturer")
                .weight(2.0)
                .maxAltitude(500)
                .maxSpeed(100)
                .batteryCapacity(5000)
                .flightTime(40)
                .cameraResolution("4K")
                .status(1)
                .build();
        
        Long id = droneService.create(dto);
        assertNotNull(id);
        
        DroneDTO result = droneService.findById(id);
        assertEquals("TestCreate", result.getName());
        assertEquals("T002", result.getModel());
    }
    
    @Test
    void testUpdate() {
        DroneDTO dto = DroneDTO.builder()
                .name("TestUpdate")
                .model("T003")
                .weight(1.5)
                .batteryCapacity(3000)
                .flightTime(25)
                .build();
        
        Long id = droneService.create(dto);
        
        DroneDTO updateDto = DroneDTO.builder()
                .name("UpdatedName")
                .model("T003")
                .weight(1.5)
                .batteryCapacity(3000)
                .flightTime(25)
                .build();
        
        droneService.update(id, updateDto);
        
        DroneDTO result = droneService.findById(id);
        assertEquals("UpdatedName", result.getName());
    }
    
    @Test
    void testUpdate_NotFound() {
        DroneDTO dto = DroneDTO.builder()
                .name("Test")
                .model("T001")
                .weight(1.0)
                .batteryCapacity(4000)
                .flightTime(30)
                .build();
        
        assertThrows(BusinessException.class, () -> droneService.update(9999L, dto));
    }
    
    @Test
    void testDelete() {
        DroneDTO dto = DroneDTO.builder()
                .name("TestDelete")
                .model("T004")
                .weight(1.0)
                .batteryCapacity(4000)
                .flightTime(30)
                .build();
        
        Long id = droneService.create(dto);
        
        droneService.delete(id);
        
        assertThrows(BusinessException.class, () -> droneService.findById(id));
    }
    
    @Test
    void testDelete_NotFound() {
        assertThrows(BusinessException.class, () -> droneService.delete(9999L));
    }
}