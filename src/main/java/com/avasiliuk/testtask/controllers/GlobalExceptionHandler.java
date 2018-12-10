package com.avasiliuk.testtask.controllers;

import com.avasiliuk.testtask.services.ApiException;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.stream.Collectors;

@ControllerAdvice
class GlobalExceptionHandler {
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<Error> handleException(ApiException e) {
        return new ResponseEntity<>(new Error(e.getMessage(), null), e.getCode());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Error> handleException(MethodArgumentNotValidException e) {
        StringBuilder message = new StringBuilder();
        message.append("Validation failure: ");
        final BindingResult bindingResult = e.getBindingResult();
        if (!CollectionUtils.isEmpty(bindingResult.getGlobalErrors())) {
            message.append(bindingResult.getGlobalErrors().stream()
                    .map(oe -> "'" + oe.getObjectName() + "' " + oe.getDefaultMessage())
                    .collect(Collectors.joining(", ")));

        }
        if (!CollectionUtils.isEmpty(bindingResult.getFieldErrors())) {
            message.append(bindingResult.getFieldErrors().stream()
                    .map(fe -> "'" + fe.getField() + "' " + fe.getDefaultMessage())
                    .collect(Collectors.joining(", ")));

        }
        return new ResponseEntity<>(new Error(message.toString(), null), HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Error> handleException(Exception e) {
        final String stackTrace = ExceptionUtils.getStackTrace(e);
        return new ResponseEntity<>(new Error(e.getMessage(), stackTrace), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Data
    @AllArgsConstructor
    static class Error {
        private String message;
        private String stackTrace;
    }
}
