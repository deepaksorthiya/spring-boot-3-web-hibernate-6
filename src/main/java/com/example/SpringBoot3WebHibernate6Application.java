package com.example;

import com.example.dto.AppUserDto;
import com.example.global.model.ErrorDto;
import com.example.global.model.FormFieldDto;
import com.example.validation.PasswordMatchingValidator;
import com.example.validation.StrongPasswordValidator;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportRuntimeHints;

@SpringBootApplication
@ImportRuntimeHints(SpringBoot3WebHibernate6Application.Hints.class)
public class SpringBoot3WebHibernate6Application {

    public static void main(String[] args) {
        SpringApplication.run(SpringBoot3WebHibernate6Application.class, args);
    }

    static class Hints implements RuntimeHintsRegistrar {

        @Override
        public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
            // register messages properties files
            hints.resources().registerResourceBundle("i18n/messages/messages");
            hints.resources().registerResourceBundle("i18n/messages/messages_hi");

            // register AppUserDto for hibernate
            hints.reflection()
                    .registerType(AppUserDto.class, MemberCategory.values());

            // register validators
            hints.reflection()
                    .registerType(StrongPasswordValidator.class, MemberCategory.values());
            hints.reflection()
                    .registerType(PasswordMatchingValidator.class, MemberCategory.values());

            // register for custom classes
            hints.reflection()
                    .registerType(FormFieldDto.class, MemberCategory.values());
            hints.reflection()
                    .registerType(ErrorDto.class, MemberCategory.values());
        }
    }

}
