package com.example.config;

import com.fasterxml.jackson.datatype.hibernate6.Hibernate6Module;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.MessageSource;
import org.springframework.context.support.ResourceBundleMessageSource;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

class WebAppConfigTest {

    @Test
    @DisplayName("Should create Hibernate6Module bean")
    void testHibernate6ModuleBean() {
        WebAppConfig config = new WebAppConfig();
        Hibernate6Module module = config.hibernate6Module();
        assertNotNull(module);
    }

    @Test
    @DisplayName("Should create and configure MessageSource bean")
    void testMessageSourceBean() {
        WebAppConfig config = new WebAppConfig();
        MessageSource messageSource = config.messageSource();
        assertNotNull(messageSource);
        assertInstanceOf(ResourceBundleMessageSource.class, messageSource);

        String messageEn = messageSource.getMessage("appuser.not.found", new Object[]{1}, Locale.ENGLISH);
        assertEquals("Resource not found in database ID : 1.", messageEn);

        String messageHi = messageSource.getMessage("appuser.not.found", new Object[]{1}, new Locale("hi"));
        assertEquals("डेटाबेस आईडी में संसाधन नहीं मिला : 1.", messageHi);
    }
}
