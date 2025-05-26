package com.zxyspace.exception;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
public class ValidationErrorDetails extends ErrorDetails {
    private final Map<String, String> errors;

    public ValidationErrorDetails(LocalDateTime timestamp, String message, String details, 
                                String errorCode, Map<String, String> errors) {
        super(timestamp, message, details, errorCode);
        this.errors = errors;
    }
} 