package com.amalstack.api.notebooks.exception;

public class EntityNotFoundException extends RuntimeException {
    static final String ERR_MSG = "The entity was not found";

    public EntityNotFoundException() {
        super(ERR_MSG);
    }

    public EntityNotFoundException(String message) {
        super(message);
    }

    public EntityNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public EntityNotFoundException(Throwable cause) {
        super(cause);
    }
}
