package com.example.validation;

import com.example.entity.AppUser;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Objects;

public class PasswordMatchingValidator implements ConstraintValidator<PasswordMatching, AppUser> {

    @Override
    public boolean isValid(AppUser appUser, ConstraintValidatorContext constraintValidatorContext) {
        String passwordValue = appUser.getPassword();
        String confirmPasswordValue = appUser.getConfirmPassword();
        return Objects.equals(passwordValue, confirmPasswordValue);
    }
}