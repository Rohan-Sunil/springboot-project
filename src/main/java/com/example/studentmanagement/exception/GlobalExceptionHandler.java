package com.example.studentmanagement.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Object> handleResourceNotFoundException(ResourceNotFoundException ex) {

        Map<String, Object> response = new HashMap<String, Object>();
        response.put("message", ex.getMessage());
        response.put("status", HttpStatus.NOT_FOUND.value());

        return new ResponseEntity<Object>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, Object> response = new HashMap<String, Object>();
        response.put("message", "Validation failed");
        response.put(
                "errors",
                ex.getBindingResult().getFieldErrors().stream()
                        .collect(Collectors.toMap(fe -> fe.getField(), fe -> fe.getDefaultMessage(), (a, b) -> a))
        );
        response.put("status", HttpStatus.BAD_REQUEST.value());
        return new ResponseEntity<Object>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGlobalException(Exception ex) {

        Map<String, Object> response = new HashMap<String, Object>();
        response.put("message", ex.getMessage());
        response.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());

        return new ResponseEntity<Object>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
