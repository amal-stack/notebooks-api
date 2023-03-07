package com.amalstack.api.notebooks.controller;

import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class ResolvedExceptionResultMatcher implements ResultMatcher {
    private final Map<String, Object> propertyMap = new HashMap<>();
    private Class<? extends Exception> expectedType;
    private String expectedMessage;

    public ResolvedExceptionResultMatcher isInstanceOf(Class<? extends Exception> type) {
        this.expectedType = type;
        return this;
    }

    public ResolvedExceptionResultMatcher hasMessage(String message, Object... params) {
        this.expectedMessage = String.format(message, params);
        return this;
    }

    public ResolvedExceptionResultMatcher hasFieldOrPropertyWithValue(String name, Object value) {
        propertyMap.put(name, value);
        return this;
    }

    @Override
    public void match(MvcResult result) {
        var throwableAssert = assertThat(result.getResolvedException());
        if (expectedType != null) {
            throwableAssert.isInstanceOf(expectedType);
        }
        if (expectedMessage != null) {
            throwableAssert.hasMessage(expectedMessage);
        }
        for (var entry : propertyMap.entrySet()) {
            throwableAssert.hasFieldOrPropertyWithValue(entry.getKey(), entry.getValue());
        }
    }
}