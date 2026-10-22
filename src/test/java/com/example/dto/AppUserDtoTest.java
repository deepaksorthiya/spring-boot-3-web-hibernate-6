package com.example.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AppUserDtoTest {

    @Test
    @DisplayName("Should test AppUserDto record components and equality")
    void testAppUserDtoRecord() {
        AppUserDto dto1 = new AppUserDto(1L, "user@test.com", "Secret@123", "First", "Last");
        AppUserDto dto2 = new AppUserDto(1L, "user@test.com", "Secret@123", "First", "Last");
        AppUserDto dto3 = new AppUserDto(2L, "other@test.com", "Other@123", "Other", "User");

        assertEquals(1L, dto1.userId());
        assertEquals("user@test.com", dto1.email());
        assertEquals("Secret@123", dto1.password());
        assertEquals("First", dto1.firstName());
        assertEquals("Last", dto1.lastName());

        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
        assertNotEquals(dto1, dto3);
        assertTrue(dto1.toString().contains("user@test.com"));
    }
}
