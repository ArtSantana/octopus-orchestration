package com.octopus.orchestration.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@AllArgsConstructor
public class ContainersException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private String message;
    private HttpStatus httpStatus;
}