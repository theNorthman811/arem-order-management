package com.arem.core.model;

import java.util.List;

public class ValidationException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    private final List<String> errors;

    public ValidationException(List<String> errors) {
        super(String.join("; ", errors));
        this.errors = errors;
    }

    public ValidationException(String message) {
        super(message);
        this.errors = List.of(message);
    }

    public List<String> getErrors() {
        return errors;
    }
} 