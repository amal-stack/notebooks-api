package com.amalstack.api.notebooks.controller;

import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class FailedValidationResultMatcher implements ResultMatcher {

    private final Map<String, String> errorMap = new HashMap<>();
    private String message = "Validation failed";

    @Override
    public void match(@NonNull MvcResult result) throws Exception {
        status().isBadRequest().match(result);
        content().contentType(MediaType.APPLICATION_JSON).match(result);

        AppResultMatchers.resolvedException()
                .isInstanceOf(MethodArgumentNotValidException.class)
                .match(result);
        jsonPath("$.message")
                .value(message)
                .match(result);

        for (Map.Entry<String, String> entry : errorMap.entrySet()) {
            jsonPath("$.errors." + entry.getKey())
                    .value(entry.getValue())
                    .match(result);
        }
    }

    public FailedValidationResultMatcher withMessage(String message) {
        this.message = message;
        return this;
    }

    public FailedValidationResultMatcher withError(String name, String message) {
        this.errorMap.put(name, message);
        return this;
    }
}
