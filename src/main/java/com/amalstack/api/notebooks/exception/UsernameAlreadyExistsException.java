package com.amalstack.api.notebooks.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.text.MessageFormat;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Username already exists")
public class UsernameAlreadyExistsException extends RuntimeException {
    static final String ERR_MSG_FORMAT = "The user with username {0} already exists";
    private final String username;


    public UsernameAlreadyExistsException(String username, Throwable cause) {
        super(MessageFormat.format(ERR_MSG_FORMAT, username), cause);
        this.username = username;
    }

    public UsernameAlreadyExistsException(String username) {
        this(username, null);
    }

    public String getUsername() {
        return username;
    }
}
