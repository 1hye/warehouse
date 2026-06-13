package com.example.uav.service;

import com.example.uav.domain.Uav;
import com.example.uav.repository.UavMapper;
import com.example.uav.service.impl.UavServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UavService 单元测试")
class UavServiceImplTest {

    @Mock
    private UavMapper uavMapper;

    @InjectMocks
    private UavServiceImpl uavService;

    private Uav testUav;

    @BeforeEach
    void setUp() {
        testUav = new Uav();
        testUav.setId(1L);
        testUav.setName("DJI Mavic 3");
        testUav.setType("多旋翼");
        testUav.setSerialNumber("SN202406001");
        testUav.setMaxFlightTime(46);
        testUav.setStatus("active");
    }

    @Test
    @DisplayName("should_return_uav_list_when_query")
    void should_return_uav_list_when_query() {
        // given
        when(uavMapper.selectUavList(any(Uav.class))).thenReturn(Arrays.asList(testUav));

        // when
        List<Uav> result = uavService.selectUavList(new Uav());

        // then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("DJI Mavic 3", result.get(0).getName());
        verify(uavMapper, times(1)).selectUavList(any(Uav.class));
    }

    @Test
    @DisplayName("should_return_uav_when_query_by_id")
    void should_return_uav_when_query_by_id() {
        // given
        when(uavMapper.selectUavById(1L)).thenReturn(testUav);

        // when
        Uav result = uavService.selectUavById(1L);

        // then
        assertNotNull(result);
        assertEquals("DJI Mavic 3", result.getName());
        verify(uavMapper, times(1)).selectUavById(1L);
    }

    @Test
    @DisplayName("should_insert_uav_when_save")
    void should_insert_uav_when_save() {
        // given
        when(uavMapper.insertUav(any(Uav.class))).thenReturn(1);

        // when
        int result = uavService.insertUav(testUav);

        // then
        assertEquals(1, result);
        assertNotNull(testUav.getCreateTime());
        assertNotNull(testUav.getUpdateTime());
        assertEquals(0, testUav.getDeleted().intValue());
        verify(uavMapper, times(1)).insertUav(any(Uav.class));
    }

    @Test
    @DisplayName("should_update_uav_when_modify")
    void should_update_uav_when_modify() {
        // given
        when(uavMapper.updateUav(any(Uav.class))).thenReturn(1);

        // when
        int result = uavService.updateUav(testUav);

        // then
        assertEquals(1, result);
        assertNotNull(testUav.getUpdateTime());
        verify(uavMapper, times(1)).updateUav(any(Uav.class));
    }

    @Test
    @DisplayName("should_delete_uav_when_remove")
    void should_delete_uav_when_remove() {
        // given
        when(uavMapper.deleteUavById(eq(1L), any())).thenReturn(1);

        // when
        int result = uavService.deleteUavById(1L);

        // then
        assertEquals(1, result);
        verify(uavMapper, times(1)).deleteUavById(eq(1L), any());
    }

    @Test
    @DisplayName("should_generate_ai_attributes_for_quadcopter")
    void should_generate_ai_attributes_for_quadcopter() {
        // when
        Uav result = uavService.generateAiAttributes("Mavic 3", "多旋翼");

        // then
        assertNotNull(result);
        assertEquals("Mavic 3", result.getName());
        assertEquals("多旋翼", result.getType());
        assertTrue(result.getMaxFlightTime() >= 25 && result.getMaxFlightTime() <= 50);
        assertTrue(result.getMaxRange() >= 5.0 && result.getMaxRange() <= 30.0);
        assertNotNull(result.getDescription());
        assertTrue(result.getDescription().contains("多旋翼"));
    }

    @Test
    @DisplayName("should_generate_ai_attributes_for_fixed_wing")
    void should_generate_ai_attributes_for_fixed_wing() {
        // when
        Uav result = uavService.generateAiAttributes("Fixed Wing Pro", "固定翼");

        // then
        assertNotNull(result);
        assertEquals("固定翼", result.getType());
        assertTrue(result.getMaxFlightTime() >= 60 && result.getMaxFlightTime() <= 180);
        assertTrue(result.getMaxRange() >= 50.0);
        assertTrue(result.getWeight() >= 2000.0);
    }

    @Test
    @DisplayName("should_return_null_description_when_no_ai_description")
    void should_return_null_description_when_no_ai_description() {
        // Simulating behavior when description field defaults
        Uav uav = new Uav("Test", "多旋翼");
        assertNull(uav.getDescription());
    }
}
