package com.example.constants;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;

import static org.junit.jupiter.api.Assertions.*;

class KeyConstantsTest {

    @Test
    @DisplayName("Should verify KeyConstants API_PREFIX value")
    void testConstantValue() {
        assertEquals("/api/v1", KeyConstants.API_PREFIX);
    }

    @Test
    @DisplayName("Should verify private constructor via reflection")
    void testPrivateConstructor() throws NoSuchMethodException {
        Constructor<KeyConstants> constructor = KeyConstants.class.getDeclaredConstructor();
        assertTrue(java.lang.reflect.Modifier.isPrivate(constructor.getModifiers()));
        constructor.setAccessible(true);
        assertDoesNotThrow(() -> {
            KeyConstants instance = constructor.newInstance();
            assertNotNull(instance);
        });
    }
}
