package com.amalstack.api.notebooks.controller;

import com.amalstack.api.notebooks.dto.ErrorInfo;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ControllerAdvice
public class MethodArgumentNotValidExceptionHandler extends ResponseEntityExceptionHandler {

    @NonNull
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            @NonNull MethodArgumentNotValidException ex,
            @NonNull HttpHeaders headers,
            @NonNull HttpStatus status,
            @NonNull WebRequest request) {

        Map<String, String> errors = Stream.concat(
                ex.getFieldErrors()
                        .stream()
                        .map(e -> Map.entry(e.getField(), e.getDefaultMessage())),
                ex.getGlobalErrors()
                        .stream()
                        .map(e -> Map.entry(e.getObjectName(), e.getDefaultMessage()))
        ).collect(Collectors.toUnmodifiableMap(
                Map.Entry::getKey,
                Map.Entry::getValue
        ));

        var response = new ErrorInfo<>(status, ex.getLocalizedMessage(), errors);
        return handleExceptionInternal(ex, response, headers, status, request);
    }
}
