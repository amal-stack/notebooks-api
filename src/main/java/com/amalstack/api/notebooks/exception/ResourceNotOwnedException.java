package com.amalstack.api.notebooks.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.UNAUTHORIZED, reason = "Current user does not have the ownership of the resource")
public class ResourceNotOwnedException extends RuntimeException {

    public ResourceNotOwnedException() {
        super("Current user does not have the privileges to access the requested resource");
    }

    public ResourceNotOwnedException(String message) {
        super(message);
    }

    public ResourceNotOwnedException(String message, Throwable cause) {
        super(message, cause);
    }
}


