package com.amalstack.api.notebooks.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "User not found")
public class AppUserNotFoundByIdException extends EntityNotFoundByIdException {
    static final String ERR_MSG_FORMAT = "The user with id {0} was not found";


    public AppUserNotFoundByIdException(long id) {
        this(id, null);
    }

    public AppUserNotFoundByIdException(long id, Throwable cause) {
        super(id);
    }
}


