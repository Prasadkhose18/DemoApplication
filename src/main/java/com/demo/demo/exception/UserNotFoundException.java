package com.demo.demo.exception;

public final class UserNotFoundException extends ApplicationException {
    public UserNotFoundException(String message){
        super(message);
    }

}
