package com.md.basePlatform.service;

import com.md.basePlatform.dto.UserDTO;
import com.md.basePlatform.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback
class UserServiceTest {

    @Autowired
    private UserService userService;

    private Long adminId;

    @BeforeEach
    void setUp() {
        UserDTO admin = userService.login("admin", "admin123");
        adminId = admin.getId();
    }

    @Test
    void testLogin_Success() {
        UserDTO user = userService.login("admin", "admin123");
        assertNotNull(user);
        assertEquals("admin", user.getUsername());
        assertNotNull(user.getId());
    }

    @Test
    void testLogin_WrongPassword() {
        assertThrows(BusinessException.class, () -> userService.login("admin", "wrongpass"));
    }

    @Test
    void testLogin_UserNotFound() {
        assertThrows(BusinessException.class, () -> userService.login("nonexistent", "pass123"));
    }

    @Test
    void testFindById_Success() {
        UserDTO user = userService.findById(adminId);
        assertNotNull(user);
        assertEquals("admin", user.getUsername());
    }

    @Test
    void testFindById_NotFound() {
        assertThrows(BusinessException.class, () -> userService.findById(9999L));
    }

    @Test
    void testFindByUsername_Success() {
        UserDTO user = userService.findByUsername("admin");
        assertNotNull(user);
        assertEquals("admin", user.getUsername());
    }

    @Test
    void testFindByUsername_NotFound() {
        assertThrows(BusinessException.class, () -> userService.findByUsername("nonexistent"));
    }
}