package com.example.global.exceptions;

import com.example.global.model.ErrorDto;
import com.example.global.model.FormFieldDto;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.error.ErrorAttributeOptions.Include;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.*;

@RestControllerAdvice
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
        List<FieldError> fieldErrors = bindingResult.getFieldErrors();
        Map<String, FormFieldDto> maps = new HashMap<>();
        for (FieldError fieldError : fieldErrors) {
            String fieldName = fieldError.getField();
            String errorMsg = fieldError.getDefaultMessage();

            if (!maps.containsKey(fieldName)) {
                List<String> errors = new ArrayList<>();
                errors.add(errorMsg);
                FormFieldDto formFieldDto = new FormFieldDto(fieldName, errors);
                maps.put(fieldName, formFieldDto);
            } else {
                List<String> errors = maps.get(fieldName).getErrorMsg();
                errors.add(errorMsg);

                FormFieldDto formFieldDto = new FormFieldDto(fieldName, errors);
                maps.put(fieldName, formFieldDto);
            }

        }

        ErrorDto errorDto = new ErrorDto("Invalid details. Please provide correct information.", LocalDateTime.now(), HttpStatus.BAD_REQUEST.value(), maps);
        return new ResponseEntity<>(errorDto, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({ResourceNotFoundException.class, EntityNotFoundException.class})
    public ResponseEntity<ErrorDto> handleResourceNotFoundException(ResourceNotFoundException resourceNotFoundException,
                                                                    WebRequest webRequest) {
        // Map<String, Object> attr = errorAttributes.getErrorAttributes(webRequest,
        // false);
        String message = messageSource.getMessage("appuser.not.found", new Object[]{String.valueOf(resourceNotFoundException.getUserId())}, LocaleContextHolder.getLocale());
        Map<String, Object> attr = errorAttributes.getErrorAttributes(webRequest, ErrorAttributeOptions
                .of(Include.EXCEPTION, Include.BINDING_ERRORS, Include.MESSAGE, Include.STACK_TRACE));
        System.out.println(attr);
        String description = webRequest.getDescription(false);
        System.out.println(description);
        ErrorDto errorDto = new ErrorDto(message, LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(), Collections.emptyMap());
        return new ResponseEntity<>(errorDto, HttpStatus.NOT_FOUND);
    }

}