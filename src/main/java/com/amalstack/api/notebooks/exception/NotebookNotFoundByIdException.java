package com.amalstack.api.notebooks.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Notebook not found")
public class NotebookNotFoundByIdException extends EntityNotFoundByIdException {

    public NotebookNotFoundByIdException(long id) {
        this(id, null);
    }

    public NotebookNotFoundByIdException(long id, Throwable cause) {
        super(id, "notebook", cause);
    }
}

