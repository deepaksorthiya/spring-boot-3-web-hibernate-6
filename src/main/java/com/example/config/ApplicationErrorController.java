package com.example.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.autoconfigure.web.ErrorProperties;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.autoconfigure.web.servlet.error.AbstractErrorController;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.Map;

/**
 * This will work only if no exception handler configured or exception not
 * handle by {@link org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler}
 * or custom exceptions handlers and
 * Internally tomcat is configured to redirect to status base error pages like 404.html or 404.jsp and if no
 * pages configured redirected to /error when
 * unhandled exception thrown
 *
 * @see org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
 * @see org.springframework.web.bind.annotation.RestControllerAdvice
 * @see org.springframework.web.bind.annotation.ControllerAdvice
 */
@RestController
public class ApplicationErrorController extends AbstractErrorController {

    private final ErrorProperties errorProperties;

    public ApplicationErrorController(ErrorAttributes errorAttributes, ServerProperties serverProperties) {
        super(errorAttributes);
        this.errorProperties = serverProperties.getError();
    }

    @RequestMapping("${server.error.path:${error.path:/error}}")
    public ResponseEntity<?> error(HttpServletRequest request, HttpServletResponse response) {
        HttpStatus status = getStatus(request);
        if (status == HttpStatus.NO_CONTENT) {
            return new ResponseEntity<>(status);
        }
        Map<String, Object> body = getErrorAttributes(request, getErrorAttributeOptions(request, MediaType.ALL));
        ProblemDetail problemDetail = ProblemDetail.forStatus(status);
        problemDetail.setType(URI.create(request.getRequestURI()));
        problemDetail.setInstance(URI.create(request.getRequestURI()));
        problemDetail.setProperties(body);
        return new ResponseEntity<>(problemDetail, status);
    }

    protected ErrorAttributeOptions getErrorAttributeOptions(HttpServletRequest request, MediaType mediaType) {
        ErrorAttributeOptions options = ErrorAttributeOptions.defaults();
        if (this.errorProperties.isIncludeException()) {
            options = options.including(ErrorAttributeOptions.Include.EXCEPTION);
        }
        if (isIncludeStackTrace(request, mediaType)) {
            options = options.including(ErrorAttributeOptions.Include.STACK_TRACE);
        }
        if (isIncludeMessage(request, mediaType)) {
            options = options.including(ErrorAttributeOptions.Include.MESSAGE);
        }
        if (isIncludeBindingErrors(request, mediaType)) {
            options = options.including(ErrorAttributeOptions.Include.BINDING_ERRORS);
        }
        options = isIncludePath(request, mediaType) ? options.including(ErrorAttributeOptions.Include.PATH) : options.excluding(ErrorAttributeOptions.Include.PATH);
        return options;
    }

    protected boolean isIncludeStackTrace(HttpServletRequest request, MediaType produces) {
        return switch (errorProperties.getIncludeStacktrace()) {
            case ALWAYS -> true;
            case ON_PARAM -> getTraceParameter(request);
            case NEVER -> false;
        };
    }

    /**
     * Determine if the message attribute should be included.
     *
     * @param request  the source request
     * @param produces the media type produced (or {@code MediaType.ALL})
     * @return if the message attribute should be included
     */
    protected boolean isIncludeMessage(HttpServletRequest request, MediaType produces) {
        return switch (errorProperties.getIncludeMessage()) {
            case ALWAYS -> true;
            case ON_PARAM -> getMessageParameter(request);
            case NEVER -> false;
        };
    }

    /**
     * Determine if the errors attribute should be included.
     *
     * @param request  the source request
     * @param produces the media type produced (or {@code MediaType.ALL})
     * @return if the errors attribute should be included
     */
    protected boolean isIncludeBindingErrors(HttpServletRequest request, MediaType produces) {
        return switch (errorProperties.getIncludeBindingErrors()) {
            case ALWAYS -> true;
            case ON_PARAM -> getErrorsParameter(request);
            case NEVER -> false;
        };
    }

    /**
     * Determine if the path attribute should be included.
     *
     * @param request  the source request
     * @param produces the media type produced (or {@code MediaType.ALL})
     * @return if the path attribute should be included
     * @since 3.3.0
     */
    protected boolean isIncludePath(HttpServletRequest request, MediaType produces) {
        return switch (errorProperties.getIncludePath()) {
            case ALWAYS -> true;
            case ON_PARAM -> getPathParameter(request);
            case NEVER -> false;
        };
    }

}
