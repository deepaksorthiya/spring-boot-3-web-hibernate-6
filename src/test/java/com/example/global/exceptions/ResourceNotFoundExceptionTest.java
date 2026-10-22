package com.example.global.exceptions;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ResourceNotFoundExceptionTest {

    @Test
    @DisplayName("Should create ResourceNotFoundException and return correct userId")
    void testResourceNotFoundException() {
        ResourceNotFoundException exception = new ResourceNotFoundException(42L);
        assertEquals(42L, exception.getUserId());
    }
}
