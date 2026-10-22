package com.example.validation;

import com.example.entity.AppUser;
import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PasswordMatchingValidatorTest {

    private PasswordMatchingValidator validator;
    private ConstraintValidatorContext context;

    @BeforeEach
    void setUp() {
        validator = new PasswordMatchingValidator();
        context = Mockito.mock(ConstraintValidatorContext.class);
    }

    @Test
    @DisplayName("Should return true when password and confirmPassword match")
    void testPasswordsMatch() {
        AppUser user = new AppUser();
        user.setPassword("Secret@123");
        user.setConfirmPassword("Secret@123");

        assertTrue(validator.isValid(user, context));
    }

    @Test
    @DisplayName("Should return false when password and confirmPassword differ")
    void testPasswordsDoNotMatch() {
        AppUser user = new AppUser();
        user.setPassword("Secret@123");
        user.setConfirmPassword("Different@123");

        assertFalse(validator.isValid(user, context));
    }

    @Test
    @DisplayName("Should return false when confirmPassword is null")
    void testConfirmPasswordIsNull() {
        AppUser user = new AppUser();
        user.setPassword("Secret@123");
        user.setConfirmPassword(null);

        assertFalse(validator.isValid(user, context));
    }

    @Test
    @DisplayName("Should return false when password is null and confirmPassword is not null")
    void testPasswordIsNull() {
        AppUser user = new AppUser();
        user.setPassword(null);
        user.setConfirmPassword("Secret@123");

        assertFalse(validator.isValid(user, context));
    }

    @Test
    @DisplayName("Should return true when both password and confirmPassword are null")
    void testBothPasswordsNull() {
        AppUser user = new AppUser();
        user.setPassword(null);
        user.setConfirmPassword(null);

        assertTrue(validator.isValid(user, context));
    }
}
