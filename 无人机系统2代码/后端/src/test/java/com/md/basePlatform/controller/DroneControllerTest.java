package com.md.basePlatform.controller;

import com.md.basePlatform.common.Result;
import com.md.basePlatform.dto.DroneDTO;
import com.md.basePlatform.dto.DroneQuery;
import com.md.basePlatform.exception.BusinessException;
import com.md.basePlatform.service.DroneService;
import com.github.pagehelper.PageInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DroneController.class)
class DroneControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private DroneService droneService;

    @Test
    void testList_ShouldReturnPagedResult() throws Exception {
        DroneDTO drone = DroneDTO.builder()
                .id(1L).name("TestDrone").model("T-100")
                .weight(1.5).batteryCapacity(4000).flightTime(30)
                .build();
        List<DroneDTO> list = Arrays.asList(drone);
        PageInfo<DroneDTO> pageInfo = new PageInfo<>(list);
        pageInfo.setTotal(1);

        when(droneService.findAll(any(DroneQuery.class), eq(1), eq(10))).thenReturn(pageInfo);

        mockMvc.perform(get("/api/drone")
                        .param("pageNum", "1")
                        .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.total").value(1));
    }

    @Test
    void testList_WithSearchKeyword() throws Exception {
        DroneDTO drone = DroneDTO.builder()
                .id(1L).name("SearchResult").model("S-100")
                .weight(1.0).batteryCapacity(3000).flightTime(20)
                .build();
        List<DroneDTO> list = Arrays.asList(drone);
        PageInfo<DroneDTO> pageInfo = new PageInfo<>(list);
        pageInfo.setTotal(1);

        when(droneService.findAll(any(DroneQuery.class), eq(1), eq(10))).thenReturn(pageInfo);

        mockMvc.perform(get("/api/drone")
                        .param("pageNum", "1")
                        .param("pageSize", "10")
                        .param("keyword", "SearchResult"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void testGetById_Success() throws Exception {
        DroneDTO drone = DroneDTO.builder()
                .id(1L).name("TestDrone").model("T-100")
                .weight(1.5).batteryCapacity(4000).flightTime(30)
                .build();

        when(droneService.findById(1L)).thenReturn(drone);

        mockMvc.perform(get("/api/drone/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.name").value("TestDrone"));
    }

    @Test
    void testGetById_NotFound() throws Exception {
        when(droneService.findById(9999L)).thenThrow(new BusinessException("无人机不存在"));

        mockMvc.perform(get("/api/drone/9999"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("无人机不存在"));
    }

    @Test
    void testCreate_Success() throws Exception {
        DroneDTO request = DroneDTO.builder()
                .name("NewDrone").model("N-100")
                .weight(2.0).batteryCapacity(5000).flightTime(40)
                .build();

        DroneDTO response = DroneDTO.builder()
                .id(1L).name("NewDrone").model("N-100")
                .weight(2.0).batteryCapacity(5000).flightTime(40)
                .build();

        when(droneService.create(any(DroneDTO.class))).thenReturn(1L);
        when(droneService.findById(1L)).thenReturn(response);

        mockMvc.perform(post("/api/drone")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value(201))
                .andExpect(jsonPath("$.data.name").value("NewDrone"));
    }

    @Test
    void testCreate_ValidationError() throws Exception {
        DroneDTO invalid = DroneDTO.builder()
                .name("")
                .model("")
                .build();

        mockMvc.perform(post("/api/drone")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdate_Success() throws Exception {
        DroneDTO request = DroneDTO.builder()
                .name("Updated").model("U-100")
                .weight(2.0).batteryCapacity(5000).flightTime(40)
                .build();

        DroneDTO response = DroneDTO.builder()
                .id(1L).name("Updated").model("U-100")
                .weight(2.0).batteryCapacity(5000).flightTime(40)
                .build();

        doNothing().when(droneService).update(anyLong(), any(DroneDTO.class));
        when(droneService.findById(1L)).thenReturn(response);

        mockMvc.perform(put("/api/drone/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.name").value("Updated"));
    }

    @Test
    void testDelete_Success() throws Exception {
        doNothing().when(droneService).delete(1L);

        mockMvc.perform(delete("/api/drone/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void testDelete_NotFound() throws Exception {
        doThrow(new BusinessException("无人机不存在")).when(droneService).delete(9999L);

        mockMvc.perform(delete("/api/drone/9999"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("无人机不存在"));
    }
}