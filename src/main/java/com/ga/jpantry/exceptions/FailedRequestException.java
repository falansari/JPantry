package com.ga.jpantry.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNPROCESSABLE_CONTENT)
public class FailedRequestException extends RuntimeException {
    public FailedRequestException(String message) {
        super(message);
    }
}
