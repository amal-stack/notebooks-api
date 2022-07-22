package com.amalstack.api.notebooks.exception;

import java.text.MessageFormat;

public class EntityNotFoundByIdException extends EntityNotFoundException {

    static final String ERR_MSG_FORMAT = "The entity with id {0} was not found";
    private final long id;

    public EntityNotFoundByIdException(long id) {
        this(id, null);
    }

    public EntityNotFoundByIdException(long id, Throwable cause) {
        super(MessageFormat.format(ERR_MSG_FORMAT, id), cause);
        this.id = id;
    }


    public long getId() {
        return id;
    }
}
