package com.demo.demo.exception;

public class InvalidStatementRequestException extends RuntimeException {

    public InvalidStatementRequestException(String message) {
        super(message);
    }
}
