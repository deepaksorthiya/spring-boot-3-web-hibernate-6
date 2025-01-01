package com.example.global.exceptions;

import com.example.global.model.ErrorDto;
import com.example.global.model.FormFieldDto;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.error.ErrorAttributeOptions.Include;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.*;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class UserControllerAdviceHandler extends ResponseEntityExceptionHandler {

    private final ErrorAttributes errorAttributes;
    private final MessageSource messageSource;

    public UserControllerAdviceHandler(ErrorAttributes errorAttributes, MessageSource messageSource) {
        this.errorAttributes = errorAttributes;
        this.messageSource = messageSource;
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException exception, HttpHeaders headers, HttpStatusCode status, WebRequest request) {

        BindingResult bindingResult = exception.getBindingResult();
        List<ObjectError> fieldErrors = bindingResult.getAllErrors();
        Map<String, FormFieldDto> maps = new HashMap<>();
        for (ObjectError fieldError : fieldErrors) {
            String fieldName = "password";
            if (fieldError instanceof FieldError error) {
                fieldName = error.getField();
            }
            String errorMsg = fieldError.getDefaultMessage();

            if (!maps.containsKey(fieldName)) {
                List<String> errors = new ArrayList<>();
                errors.add(errorMsg);
                FormFieldDto formFieldDto = new FormFieldDto(fieldName, errors);
                maps.put(fieldName, formFieldDto);
            } else {
                List<String> errors = maps.get(fieldName).getErrors();
                errors.add(errorMsg);

                FormFieldDto formFieldDto = new FormFieldDto(fieldName, errors);
                maps.put(fieldName, formFieldDto);
            }

        }

        ErrorDto errorDto = new ErrorDto("Invalid details. Please provide correct information.", LocalDateTime.now(), HttpStatus.BAD_REQUEST.value(), maps);
        return new ResponseEntity<>(errorDto, headers, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({ResourceNotFoundException.class})
    public ResponseEntity<Object> handleResourceNotFoundException(ResourceNotFoundException resourceNotFoundException, WebRequest request) {
        String message = messageSource.getMessage("appuser.not.found", new Object[]{String.valueOf(resourceNotFoundException.getUserId())}, LocaleContextHolder.getLocale());
        Map<String, Object> attr = errorAttributes.getErrorAttributes(request, ErrorAttributeOptions
                .of(Include.EXCEPTION, Include.BINDING_ERRORS, Include.MESSAGE, Include.STACK_TRACE, Include.PATH, Include.STATUS, Include.ERROR));
        log.info("{}", attr);
        log.info("{}", message);
        ProblemDetail body = createProblemDetail(resourceNotFoundException, HttpStatus.NOT_FOUND, "Validation failed", "appuser.not.found", new Object[]{String.valueOf(resourceNotFoundException.getUserId())}, request);
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        Assert.state(requestAttributes != null, "Could not find current request via RequestContextHolder");
        Assert.isInstanceOf(ServletRequestAttributes.class, requestAttributes);
        HttpServletRequest servletRequest = ((ServletRequestAttributes) requestAttributes).getRequest();
        Assert.state(servletRequest != null, "Could not find current HttpServletRequest");
        body.setType(URI.create(servletRequest.getRequestURI()));
//        ErrorDto errorDto = new ErrorDto(message, LocalDateTime.now(),
//                HttpStatus.NOT_FOUND.value(), Collections.emptyMap());
//        return new ResponseEntity<>(errorDto, HttpStatus.NOT_FOUND);
        ResponseEntity<Object> entity = handleExceptionInternal(resourceNotFoundException, body, new HttpHeaders(), HttpStatus.NOT_FOUND, request);
        return entity;
    }

}