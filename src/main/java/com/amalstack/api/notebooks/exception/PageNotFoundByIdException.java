package com.amalstack.api.notebooks.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Notebook page not found")
public class PageNotFoundByIdException extends EntityNotFoundByIdException {
    public static final String ERR_MSG_FORMAT = "The notebook page with id {0} was not found";

    public PageNotFoundByIdException(long id) {
        this(id, null);
    }

    public PageNotFoundByIdException(long id, Throwable cause) {
        super(id);
    }
}

