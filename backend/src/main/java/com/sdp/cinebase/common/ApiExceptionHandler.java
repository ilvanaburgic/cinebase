package com.sdp.cinebase.common;

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

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> onValidationError(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError fe : ex.getBindingResult().getFieldErrors()) {
            errors.put(fe.getField(), fe.getDefaultMessage());
        }
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

        return ResponseEntity
                .status(code)
                .body(Map.of(
                        "status",  code.value(),
                        "error",   reason,
                        "message", message
                ));
    }
}
