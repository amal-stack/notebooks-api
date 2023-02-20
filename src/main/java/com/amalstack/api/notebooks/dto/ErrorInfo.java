package com.amalstack.api.notebooks.dto;

import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

public record ErrorInfo<T>(
        int status,
        String error,
        LocalDateTime timestamp,
        String message,
        T errors) {

    public ErrorInfo(HttpStatus status, String message) {
        this(status, message, null);
    }

    public ErrorInfo(HttpStatus status, String message, T data) {
        this(status.value(), status.name(), LocalDateTime.now(), message, data);
    }
}
