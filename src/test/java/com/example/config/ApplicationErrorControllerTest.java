package com.example.config;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.autoconfigure.web.ErrorProperties;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ApplicationErrorControllerTest {

    @Mock
    private ErrorAttributes errorAttributes;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    private ServerProperties serverProperties;
    private ApplicationErrorController errorController;

    @BeforeEach
    void setUp() {
        serverProperties = new ServerProperties();
        errorController = new ApplicationErrorController(errorAttributes, serverProperties);
    }

    @Test
    @DisplayName("Should return NO_CONTENT when request status is NO_CONTENT")
    void testError_NoContent() {
        when(request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE)).thenReturn(204);

        ResponseEntity<?> result = errorController.error(request, response);

        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
        assertNull(result.getBody());
    }

    @Test
    @DisplayName("Should return ProblemDetail when request status is error status")
    void testError_StandardError() {
        when(request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE)).thenReturn(500);
        when(request.getRequestURI()).thenReturn("/error");
        when(errorAttributes.getErrorAttributes(any(org.springframework.web.context.request.WebRequest.class), any(ErrorAttributeOptions.class)))
                .thenReturn(Map.of("message", "Internal Server Error", "status", 500));

        ResponseEntity<?> result = errorController.error(request, response);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
        assertInstanceOf(ProblemDetail.class, result.getBody());

        ProblemDetail problemDetail = (ProblemDetail) result.getBody();
        assertEquals(500, problemDetail.getStatus());
        assertEquals("/error", problemDetail.getType().toString());
        assertEquals("/error", problemDetail.getInstance().toString());
    }

    @Test
    @DisplayName("Should test stacktrace inclusion for ALWAYS, NEVER, ON_PARAM")
    void testIncludeStackTrace() {
        serverProperties.getError().setIncludeStacktrace(ErrorProperties.IncludeAttribute.ALWAYS);
        assertTrue(errorController.isIncludeStackTrace(request, MediaType.ALL));

        serverProperties.getError().setIncludeStacktrace(ErrorProperties.IncludeAttribute.NEVER);
        assertFalse(errorController.isIncludeStackTrace(request, MediaType.ALL));

        serverProperties.getError().setIncludeStacktrace(ErrorProperties.IncludeAttribute.ON_PARAM);
        when(request.getParameter("trace")).thenReturn("true");
        assertTrue(errorController.isIncludeStackTrace(request, MediaType.ALL));

        when(request.getParameter("trace")).thenReturn(null);
        assertFalse(errorController.isIncludeStackTrace(request, MediaType.ALL));
    }

    @Test
    @DisplayName("Should test message inclusion for ALWAYS, NEVER, ON_PARAM")
    void testIncludeMessage() {
        serverProperties.getError().setIncludeMessage(ErrorProperties.IncludeAttribute.ALWAYS);
        assertTrue(errorController.isIncludeMessage(request, MediaType.ALL));

        serverProperties.getError().setIncludeMessage(ErrorProperties.IncludeAttribute.NEVER);
        assertFalse(errorController.isIncludeMessage(request, MediaType.ALL));

        serverProperties.getError().setIncludeMessage(ErrorProperties.IncludeAttribute.ON_PARAM);
        when(request.getParameter("message")).thenReturn("true");
        assertTrue(errorController.isIncludeMessage(request, MediaType.ALL));

        when(request.getParameter("message")).thenReturn(null);
        assertFalse(errorController.isIncludeMessage(request, MediaType.ALL));
    }

    @Test
    @DisplayName("Should test binding errors inclusion for ALWAYS, NEVER, ON_PARAM")
    void testIncludeBindingErrors() {
        serverProperties.getError().setIncludeBindingErrors(ErrorProperties.IncludeAttribute.ALWAYS);
        assertTrue(errorController.isIncludeBindingErrors(request, MediaType.ALL));

        serverProperties.getError().setIncludeBindingErrors(ErrorProperties.IncludeAttribute.NEVER);
        assertFalse(errorController.isIncludeBindingErrors(request, MediaType.ALL));

        serverProperties.getError().setIncludeBindingErrors(ErrorProperties.IncludeAttribute.ON_PARAM);
        when(request.getParameter("errors")).thenReturn("true");
        assertTrue(errorController.isIncludeBindingErrors(request, MediaType.ALL));

        when(request.getParameter("errors")).thenReturn(null);
        assertFalse(errorController.isIncludeBindingErrors(request, MediaType.ALL));
    }

    @Test
    @DisplayName("Should test path inclusion for ALWAYS, NEVER, ON_PARAM")
    void testIncludePath() {
        serverProperties.getError().setIncludePath(ErrorProperties.IncludeAttribute.ALWAYS);
        assertTrue(errorController.isIncludePath(request, MediaType.ALL));

        serverProperties.getError().setIncludePath(ErrorProperties.IncludeAttribute.NEVER);
        assertFalse(errorController.isIncludePath(request, MediaType.ALL));

        serverProperties.getError().setIncludePath(ErrorProperties.IncludeAttribute.ON_PARAM);
        when(request.getParameter("path")).thenReturn("true");
        assertTrue(errorController.isIncludePath(request, MediaType.ALL));

        when(request.getParameter("path")).thenReturn(null);
        assertFalse(errorController.isIncludePath(request, MediaType.ALL));
    }

    @Test
    @DisplayName("Should build ErrorAttributeOptions correctly based on server properties")
    void testGetErrorAttributeOptions() {
        serverProperties.getError().setIncludeException(true);
        serverProperties.getError().setIncludeStacktrace(ErrorProperties.IncludeAttribute.ALWAYS);
        serverProperties.getError().setIncludeMessage(ErrorProperties.IncludeAttribute.ALWAYS);
        serverProperties.getError().setIncludeBindingErrors(ErrorProperties.IncludeAttribute.ALWAYS);
        serverProperties.getError().setIncludePath(ErrorProperties.IncludeAttribute.ALWAYS);

        ErrorAttributeOptions options = errorController.getErrorAttributeOptions(request, MediaType.ALL);

        assertTrue(options.isIncluded(ErrorAttributeOptions.Include.EXCEPTION));
        assertTrue(options.isIncluded(ErrorAttributeOptions.Include.STACK_TRACE));
        assertTrue(options.isIncluded(ErrorAttributeOptions.Include.MESSAGE));
        assertTrue(options.isIncluded(ErrorAttributeOptions.Include.BINDING_ERRORS));
        assertTrue(options.isIncluded(ErrorAttributeOptions.Include.PATH));
    }
}
