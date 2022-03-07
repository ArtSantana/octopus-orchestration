package com.octopus.orchestration.exceptions;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import org.apache.log4j.Logger;

@RestControllerAdvice
public class ContainersExceptionHandler {

    private static final Logger LOGGER = Logger.getLogger(ContainersExceptionHandler.class);

    @ExceptionHandler({ContainersException.class})
    protected ResponseEntity<Object> handleBaseException(ContainersException ex) {
        LOGGER.error(ex.getMessage());
        return ResponseEntity.status(ex.getHttpStatus()).body(ex.getMessage());
    }
}
