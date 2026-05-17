package com.example.wms.exception;

import java.util.Map;

public class ValidationException extends RuntimeException {
    private final Map<String, Object> validationErrors;

    public ValidationException(String message, Map<String, Object> validationErrors) {
        super(message);
        this.validationErrors = validationErrors;
    }

    public Map<String, Object> getValidationErrors() {
        return validationErrors;
    }
}
