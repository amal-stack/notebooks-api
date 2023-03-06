package com.amalstack.api.notebooks.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Section not found")
public class SectionNotFoundByIdException extends EntityNotFoundByIdException {

    public SectionNotFoundByIdException(long id) {
        this(id, null);
    }

    public SectionNotFoundByIdException(long id, Throwable cause) {
        super(id, "section", cause);
    }
}


