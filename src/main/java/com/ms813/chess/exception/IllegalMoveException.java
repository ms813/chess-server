package com.ms813.chess.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
public class IllegalMoveException extends RuntimeException {
    public IllegalMoveException(String message) {
        super(message);
    }
}
