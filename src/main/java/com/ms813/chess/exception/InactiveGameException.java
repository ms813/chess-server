package com.ms813.chess.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
public class InactiveGameException extends RuntimeException {
    public InactiveGameException(String message) {
        super(message);
    }
}
