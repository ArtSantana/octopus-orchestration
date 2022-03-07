package com.octopus.orchestration.exceptions;

import org.apache.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class BaseExceptionHandler {

    private static final Logger LOGGER = Logger.getLogger(BaseExceptionHandler.class);

    @ExceptionHandler({BaseException.class})
    protected ResponseEntity<Object> handleBaseException(BaseException ex) {
        LOGGER.error(ex.getMessage());
        return ResponseEntity.status(ex.getHttpStatus()).body(ex.getMessage());
    }
}
