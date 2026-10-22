package com.example.validation;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StrongPasswordValidatorTest {

    private StrongPasswordValidator validator;
    private ConstraintValidatorContext context;

    @BeforeEach
    void setUp() {
        validator = new StrongPasswordValidator();
        context = Mockito.mock(ConstraintValidatorContext.class);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "Password@123",
            "Strong#Pass1",
            "A1b$cdef",
            "SuperSecret!99",
            "User%Pass8",
            "MyPassword&1",
            "Test+Word9",
            "Valid=Pass1",
            "Star*Pass8",
            "P(aren123)"
    })
    @DisplayName("Should return true for valid strong passwords")
    void testValidStrongPasswords(String password) {
        assertTrue(validator.isValid(password, context));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "password@123",  // missing uppercase
            "PASSWORD@123",  // missing lowercase
            "Password1234",  // missing special character
            "Password@",     // missing digit
            "P@s1",          // too short (< 8 chars)
            "",              // empty string
            "        ",      // spaces only
            "Short1!"        // 7 characters long
    })
    @DisplayName("Should return false for invalid passwords")
    void testInvalidPasswords(String password) {
        assertFalse(validator.isValid(password, context));
    }

    @Test
    @DisplayName("Should return false when password is null")
    void testNullPassword() {
        assertFalse(validator.isValid(null, context));
    }
}
