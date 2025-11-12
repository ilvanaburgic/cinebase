package com.sdp.cinebase.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class ApiExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(ApiExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> onValidationError(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError fe : ex.getBindingResult().getFieldErrors()) {
            errors.put(fe.getField(), fe.getDefaultMessage());
        }

        log.warn("Validation error: {}", errors);

        return ResponseEntity.badRequest().body(Map.of(
                "status", 400,
                "error", "Validation failed",
                "fields", errors
        ));
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<?> onResponseStatus(ResponseStatusException ex) {
        var code = ex.getStatusCode();
        String reason =
                (code instanceof org.springframework.http.HttpStatus hs)
                        ? hs.getReasonPhrase()
                        : code.toString();

        String message = (ex.getReason() != null) ? ex.getReason() : reason;

        log.error("ResponseStatusException: {} - {}", code, message);

        return ResponseEntity
                .status(code)
                .body(Map.of(
                        "status",  code.value(),
                        "error",   reason,
                        "message", message
                ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> onGenericException(Exception ex) {
        log.error("Unexpected exception occurred", ex);

        return ResponseEntity
                .status(500)
                .body(Map.of(
                        "status", 500,
                        "error", "Internal Server Error",
                        "message", "An unexpected error occurred"
                ));
    }
}