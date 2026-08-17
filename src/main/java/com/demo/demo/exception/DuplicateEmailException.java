package com.demo.demo.exception;

public final class DuplicateEmailException extends ApplicationException {
    public DuplicateEmailException(String message){
        super(message);
    }
}
