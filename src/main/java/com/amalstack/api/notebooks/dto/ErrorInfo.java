package com.amalstack.api.notebooks.dto;

import org.springframework.http.HttpStatusCode;

import java.time.LocalDateTime;

public record ErrorInfo<T>(
        int status,
        String error,
        LocalDateTime timestamp,
        String message,
        T errors) {

    public ErrorInfo(HttpStatusCode status, String message, T data) {
        this(status.value(), status.toString(), LocalDateTime.now(), message, data);
    }
}
