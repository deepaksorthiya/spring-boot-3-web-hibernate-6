package com.example.global.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FormFieldDtoTest {

    @Test
    @DisplayName("Should test all getters, setters and constructors of FormFieldDto")
    void testFormFieldDto() {
        FormFieldDto dto = new FormFieldDto("password", List.of("Password cannot be empty", "Too short"));
        assertEquals("password", dto.getFieldName());
        assertEquals(2, dto.getErrors().size());
        assertTrue(dto.toString().contains("password"));

        // No-arg constructor & setters
        FormFieldDto empty = new FormFieldDto();
        empty.setFieldName("email");
        empty.setErrors(List.of("Invalid format"));

        assertEquals("email", empty.getFieldName());
        assertEquals(1, empty.getErrors().size());
        assertEquals("Invalid format", empty.getErrors().get(0));
    }
}
