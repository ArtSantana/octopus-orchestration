package com.octopus.orchestration.exceptions;

import org.springframework.http.HttpStatus;

public class DockerIllegalArgumentException extends BaseException {
    public DockerIllegalArgumentException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
