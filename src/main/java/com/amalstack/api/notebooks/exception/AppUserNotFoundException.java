package com.amalstack.api.notebooks.exception;

public class AppUserNotFoundException extends EntityNotFoundException {
    static final String ERR_MSG = "The user was not found";

    public AppUserNotFoundException() {
        super(ERR_MSG);
    }

    public AppUserNotFoundException(String message) {
        super(message);
    }

    public AppUserNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public AppUserNotFoundException(Throwable cause) {
        super(cause);
    }
}
