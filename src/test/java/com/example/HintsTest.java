package com.example;

import com.example.dto.AppUserDto;
import com.example.global.model.ErrorDto;
import com.example.global.model.FormFieldDto;
import com.example.validation.PasswordMatchingValidator;
import com.example.validation.StrongPasswordValidator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.aot.hint.RuntimeHints;

import static org.assertj.core.api.Assertions.assertThat;

class HintsTest {

    @Test
    @DisplayName("Should register runtime hints for bundles and reflection types")
    void testRegisterHints() {
        SpringBoot3WebHibernate6Application.Hints hintsRegistrar = new SpringBoot3WebHibernate6Application.Hints();
        RuntimeHints hints = new RuntimeHints();

        hintsRegistrar.registerHints(hints, getClass().getClassLoader());

        // Check resource bundles
        assertThat(hints.resources().resourceBundleHints())
                .anyMatch(bundle -> bundle.getBaseName().equals("i18n/messages/messages"));
        assertThat(hints.resources().resourceBundleHints())
                .anyMatch(bundle -> bundle.getBaseName().equals("i18n/messages/messages_hi"));

        // Check reflection registrations
        assertThat(hints.reflection().typeHints())
                .anyMatch(typeHint -> typeHint.getType().equals(org.springframework.aot.hint.TypeReference.of(AppUserDto.class)));
        assertThat(hints.reflection().typeHints())
                .anyMatch(typeHint -> typeHint.getType().equals(org.springframework.aot.hint.TypeReference.of(StrongPasswordValidator.class)));
        assertThat(hints.reflection().typeHints())
                .anyMatch(typeHint -> typeHint.getType().equals(org.springframework.aot.hint.TypeReference.of(PasswordMatchingValidator.class)));
        assertThat(hints.reflection().typeHints())
                .anyMatch(typeHint -> typeHint.getType().equals(org.springframework.aot.hint.TypeReference.of(FormFieldDto.class)));
        assertThat(hints.reflection().typeHints())
                .anyMatch(typeHint -> typeHint.getType().equals(org.springframework.aot.hint.TypeReference.of(ErrorDto.class)));
    }
}
