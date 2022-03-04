package com.octopus.orchestration.exceptions;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ContainersException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private String message;
    private HttpStatus httpStatus;
}