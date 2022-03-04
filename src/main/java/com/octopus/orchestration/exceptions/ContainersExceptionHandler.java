package com.octopus.orchestration.exceptions;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ContainersExceptionHandler {

    @ExceptionHandler({ContainersException.class})
    protected ResponseEntity<Object> handleBaseException(ContainersException ex) {
        return ResponseEntity.status(ex.getHttpStatus()).body(ex.getMessage());
    }
}
