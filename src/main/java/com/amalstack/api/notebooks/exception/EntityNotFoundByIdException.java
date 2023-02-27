package com.amalstack.api.notebooks.exception;

import java.text.MessageFormat;

public class EntityNotFoundByIdException extends EntityNotFoundException {

    static final String ERR_MSG_FORMAT = "The {0} with id {1} was not found";

    static final String DEFAULT_ENTITY_NAME = "entity";

    private final long id;

    public EntityNotFoundByIdException(long id) {
        this(id, DEFAULT_ENTITY_NAME, null);
    }

    public EntityNotFoundByIdException(long id, Throwable cause) {
        this(id, DEFAULT_ENTITY_NAME, cause);
    }

    public EntityNotFoundByIdException(long id, String entityName) {
        this(id, entityName, null);
    }

    public EntityNotFoundByIdException(long id, String entityName, Throwable cause) {
        super(MessageFormat.format(ERR_MSG_FORMAT, entityName, id), cause);
        this.id = id;
    }

    public long getId() {
        return id;
    }
}
