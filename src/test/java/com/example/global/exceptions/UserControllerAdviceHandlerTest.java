package com.example.global.exceptions;

import com.example.global.model.FormFieldDto;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.context.MessageSource;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.request.WebRequest;

import java.net.URI;
import java.util.Locale;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserControllerAdviceHandlerTest {

    @Mock
    private ErrorAttributes errorAttributes;

    @Mock
    private MessageSource messageSource;

    @Mock
    private WebRequest webRequest;

    @Mock
    private HttpServletRequest httpServletRequest;

    private UserControllerAdviceHandler adviceHandler;

    @BeforeEach
    void setUp() {
        adviceHandler = new UserControllerAdviceHandler(errorAttributes, messageSource);
    }

    @AfterEach
    void tearDown() {
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    @DisplayName("Should handle MethodArgumentNotValidException and format field errors into ProblemDetail")
    void testHandleMethodArgumentNotValid() throws NoSuchMethodException {
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "target");
        bindingResult.addError(new FieldError("target", "email", "Email cannot be empty"));
        bindingResult.addError(new FieldError("target", "email", "Must be a valid email"));
        bindingResult.addError(new ObjectError("target", "Global validation error"));

        MethodParameter parameter = new MethodParameter(
                UserControllerAdviceHandlerTest.class.getDeclaredMethod("dummyMethod", String.class), 0);
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(parameter, bindingResult);

        ResponseEntity<Object> response = adviceHandler.handleMethodArgumentNotValid(
                ex, new HttpHeaders(), HttpStatus.BAD_REQUEST, webRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertInstanceOf(ProblemDetail.class, response.getBody());

        ProblemDetail problemDetail = (ProblemDetail) response.getBody();
        assertNotNull(problemDetail.getProperties());
        assertTrue(problemDetail.getProperties().containsKey("errors"));

        @SuppressWarnings("unchecked")
        Map<String, FormFieldDto> errorsMap = (Map<String, FormFieldDto>) problemDetail.getProperties().get("errors");

        assertThat(errorsMap).containsKey("email");
        assertThat(errorsMap.get("email").getErrors()).containsExactly("Email cannot be empty", "Must be a valid email");
        assertThat(errorsMap).containsKey("password");
        assertThat(errorsMap.get("password").getErrors()).contains("Global validation error");
    }

    @Test
    @DisplayName("Should handle ResourceNotFoundException and return 404 ProblemDetail with URI")
    void testHandleResourceNotFoundException() {
        ResourceNotFoundException ex = new ResourceNotFoundException(42L);

        when(messageSource.getMessage(eq("appuser.not.found"), any(Object[].class), any(Locale.class)))
                .thenReturn("Resource not found in database ID : 42.");
        when(errorAttributes.getErrorAttributes(any(WebRequest.class), any()))
                .thenReturn(Map.of("message", "Not Found", "status", 404));

        when(httpServletRequest.getRequestURI()).thenReturn("/api/v1/users/42");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(httpServletRequest));

        ResponseEntity<Object> response = adviceHandler.handleResourceNotFoundException(ex, webRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertInstanceOf(ProblemDetail.class, response.getBody());

        ProblemDetail problemDetail = (ProblemDetail) response.getBody();
        assertEquals(URI.create("/api/v1/users/42"), problemDetail.getType());
        assertEquals(404, problemDetail.getStatus());
    }

    private void dummyMethod(String param) {
    }
}
