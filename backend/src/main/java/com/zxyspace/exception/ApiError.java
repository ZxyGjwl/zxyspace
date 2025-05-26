package com.zxyspace.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiError {

    private HttpStatus status;
    private String message;
    private String path;
    private LocalDateTime timestamp = LocalDateTime.now();
    private List<String> errors = new ArrayList<>();

    public ApiError(HttpStatus status, String message, String path) {
        this.status = status;
        this.message = message;
        this.path = path;
    }

    public void addError(String error) {
        this.errors.add(error);
    }
} 