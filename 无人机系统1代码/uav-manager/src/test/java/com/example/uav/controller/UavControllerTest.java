package com.example.uav.controller;

import com.example.uav.domain.Uav;
import com.example.uav.service.IUavService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UavController 单元测试")
class UavControllerTest {

    private MockMvc mockMvc;

    @Mock
    private IUavService uavService;

    @InjectMocks
    private UavController uavController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(uavController).build();
    }

    @Test
    @DisplayName("should_return_uav_list_page_when_get_list_page")
    void should_return_uav_list_page_when_get_list_page() throws Exception {
        when(uavService.selectUavList(any(Uav.class))).thenReturn(Arrays.asList(createTestUav()));

        mockMvc.perform(get("/uav/list/page")
                        .param("pageNum", "1")
                        .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    @Test
    @DisplayName("should_return_uav_detail_when_get_info")
    void should_return_uav_detail_when_get_info() throws Exception {
        when(uavService.selectUavById(1L)).thenReturn(createTestUav());

        mockMvc.perform(get("/uav/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.name").value("DJI Mavic 3"));
    }

    @Test
    @DisplayName("should_create_uav_when_add")
    void should_create_uav_when_add() throws Exception {
        when(uavService.insertUav(any(Uav.class))).thenReturn(1);

        mockMvc.perform(post("/uav/add")
                        .param("name", "DJI Mavic 3")
                        .param("type", "多旋翼"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    @Test
    @DisplayName("should_return_400_when_add_without_name")
    void should_return_400_when_add_without_name() throws Exception {
        mockMvc.perform(post("/uav/add")
                        .param("type", "多旋翼"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("should_update_uav_when_edit")
    void should_update_uav_when_edit() throws Exception {
        when(uavService.updateUav(any(Uav.class))).thenReturn(1);

        mockMvc.perform(put("/uav/edit")
                        .param("id", "1")
                        .param("name", "DJI Mavic 3 Updated")
                        .param("type", "多旋翼"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    @Test
    @DisplayName("should_delete_uav_when_remove")
    void should_delete_uav_when_remove() throws Exception {
        when(uavService.deleteUavById(1L)).thenReturn(1);

        mockMvc.perform(delete("/uav/remove/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    @Test
    @DisplayName("should_generate_ai_attributes_when_ai_generate")
    void should_generate_ai_attributes_when_ai_generate() throws Exception {
        Uav aiUav = new Uav("Mavic 3", "多旋翼");
        aiUav.setMaxFlightTime(46);
        aiUav.setMaxRange(30.0);
        aiUav.setDescription("AI 生成描述");
        when(uavService.generateAiAttributes("Mavic 3", "多旋翼")).thenReturn(aiUav);

        mockMvc.perform(get("/uav/aiGenerate")
                        .param("name", "Mavic 3")
                        .param("type", "多旋翼"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.name").value("Mavic 3"))
                .andExpect(jsonPath("$.data.maxFlightTime").value(46));
    }

    private Uav createTestUav() {
        Uav uav = new Uav();
        uav.setId(1L);
        uav.setName("DJI Mavic 3");
        uav.setType("多旋翼");
        uav.setSerialNumber("SN202406001");
        uav.setMaxFlightTime(46);
        uav.setStatus("active");
        return uav;
    }
}
