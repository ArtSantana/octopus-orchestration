package com.octopus.orchestration.exceptions;

import java.io.Serial;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class BaseException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    private String message;
    private HttpStatus httpStatus;
}