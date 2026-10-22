package com.example.global.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ErrorDtoTest {

    @Test
    @DisplayName("Should test all getters, setters and constructors of ErrorDto")
    void testErrorDto() {
        LocalDateTime now = LocalDateTime.now();
        Map<String, FormFieldDto> fieldErrors = new HashMap<>();
        fieldErrors.put("email", new FormFieldDto("email", List.of("Invalid email")));

        ErrorDto errorDto = new ErrorDto("Error occurred", now, 400, fieldErrors);

        assertEquals("Error occurred", errorDto.getMessage());
        assertEquals(now, errorDto.getTimestamp());
        assertEquals(400, errorDto.getStatus());
        assertEquals(fieldErrors, errorDto.getFieldErrors());
        assertTrue(errorDto.toString().contains("Error occurred"));

        // No-arg constructor & setters
        ErrorDto empty = new ErrorDto();
        empty.setMessage("New error");
        empty.setTimestamp(now);
        empty.setStatus(500);
        empty.setFieldErrors(Map.of());

        assertEquals("New error", empty.getMessage());
        assertEquals(now, empty.getTimestamp());
        assertEquals(500, empty.getStatus());
        assertTrue(empty.getFieldErrors().isEmpty());
    }
}
