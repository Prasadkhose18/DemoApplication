package com.demo.demo.exception;

public final class UnauthorizedAccessException extends ApplicationException {
    public UnauthorizedAccessException(String message){
        super(message);
    }
}
