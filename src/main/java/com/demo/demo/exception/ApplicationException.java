package com.demo.demo.exception;

public sealed class ApplicationException extends RuntimeException
        permits DuplicateEmailException,
        DuplicateMobileNumberException,
        InsufficientBalanceException,
        InvalidCredentialsException,
        InvalidInputException,
        InvalidStatementRequestException,
        InvalidTransactionException,
        ResourceNotFoundException,
        StatementEmailDeliveryException,
        UnauthorizedAccessException,
        UserNotFoundException,
        ValidationException {

    protected ApplicationException(String message) {
        super(message);
    }

    protected ApplicationException(String message, Throwable cause) {
        super(message, cause);
    }
}
