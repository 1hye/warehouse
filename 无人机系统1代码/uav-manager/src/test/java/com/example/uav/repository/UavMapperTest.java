package com.example.uav.repository;

import com.example.uav.domain.Uav;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DisplayName("UavMapper 数据访问层测试")
class UavMapperTest {

    @Autowired
    private UavMapper uavMapper;

    private Uav testUav;

    @BeforeEach
    void setUp() {
        testUav = new Uav();
        testUav.setName("Test Drone");
        testUav.setType("多旋翼");
        testUav.setSerialNumber("SN-TEST-001");
        testUav.setMaxFlightTime(30);
        testUav.setMaxRange(15.0);
        testUav.setStatus("active");
        testUav.setCreateTime(new Date());
        testUav.setUpdateTime(new Date());
        testUav.setDeleted(0);
    }

    @Test
    @DisplayName("should_insert_and_select_uav")
    void should_insert_and_select_uav() {
        // when
        int insertResult = uavMapper.insertUav(testUav);

        // then
        assertEquals(1, insertResult);
        assertNotNull(testUav.getId());

        // when
        Uav result = uavMapper.selectUavById(testUav.getId());

        // then
        assertNotNull(result);
        assertEquals("Test Drone", result.getName());
        assertEquals("多旋翼", result.getType());
    }

    @Test
    @DisplayName("should_select_uav_list")
    void should_select_uav_list() {
        // given
        uavMapper.insertUav(testUav);

        // when
        List<Uav> list = uavMapper.selectUavList(new Uav());

        // then
        assertFalse(list.isEmpty());
        assertTrue(list.stream().anyMatch(u -> "Test Drone".equals(u.getName())));
    }

    @Test
    @DisplayName("should_update_uav")
    void should_update_uav() {
        // given
        uavMapper.insertUav(testUav);

        // when
        testUav.setName("Updated Drone");
        testUav.setUpdateTime(new Date());
        int updateResult = uavMapper.updateUav(testUav);

        // then
        assertEquals(1, updateResult);
        Uav updated = uavMapper.selectUavById(testUav.getId());
        assertEquals("Updated Drone", updated.getName());
    }

    @Test
    @DisplayName("should_logically_delete_uav")
    void should_logically_delete_uav() {
        // given
        uavMapper.insertUav(testUav);

        // when
        int deleteResult = uavMapper.deleteUavById(testUav.getId(), new Date());

        // then
        assertEquals(1, deleteResult);

        // 逻辑删除后，查询应返回 null（因 SQL 中 WHERE deleted = 0）
        Uav deleted = uavMapper.selectUavById(testUav.getId());
        assertNull(deleted);
    }
}
