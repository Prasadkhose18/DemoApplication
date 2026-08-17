package com.demo.demo.exception;

import com.demo.demo.dto.response.ApiResponse;
import com.demo.demo.util.APIResponseBuilder;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    private final APIResponseBuilder responseBuilder;

    public GlobalExceptionHandler(APIResponseBuilder responseBuilder) {
        this.responseBuilder = responseBuilder;
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleUserNotFound(
            UserNotFoundException ex,
            HttpServletRequest request) {

        log.warn("User not found: {}", ex.getMessage());

        return responseBuilder.notFound(
                ex.getMessage(),
                request.getRequestURI()
        );
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleResourceNotFound(
            ResourceNotFoundException ex,
            HttpServletRequest request) {

        log.warn("Resource not found: {}", ex.getMessage());

        return responseBuilder.notFound(
                ex.getMessage(),
                request.getRequestURI()
        );
    }

    @ExceptionHandler(DuplicateEmailException.class)
    public ResponseEntity<ApiResponse<Void>> handleDuplicateEmail(
            DuplicateEmailException ex,
            HttpServletRequest request) {

        log.warn("Duplicate email: {}", ex.getMessage());

        return responseBuilder.conflict(
                ex.getMessage(),
                request.getRequestURI()
        );
    }

    @ExceptionHandler(DuplicateMobileNumberException.class)
    public ResponseEntity<ApiResponse<Void>> handleDuplicateMobile(
            DuplicateMobileNumberException ex,
            HttpServletRequest request) {

        log.warn("Duplicate mobile number: {}", ex.getMessage());

        return responseBuilder.conflict(
                ex.getMessage(),
                request.getRequestURI()
        );
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ApiResponse<Void>> handleInvalidCredentials(
            InvalidCredentialsException ex,
            HttpServletRequest request) {

        log.warn("Authentication failed: {}", ex.getMessage());

        return responseBuilder.error(
                HttpStatus.UNAUTHORIZED,
                ex.getMessage(),
                request.getRequestURI()
        );
    }

    @ExceptionHandler(InvalidTransactionException.class)
    public ResponseEntity<ApiResponse<Void>> handleInvalidTransaction(
            InvalidTransactionException ex,
            HttpServletRequest request) {

        log.warn("Invalid transaction: {}", ex.getMessage());

        return responseBuilder.badRequest(
                ex.getMessage(),
                request.getRequestURI()
        );
    }

    @ExceptionHandler(InvalidStatementRequestException.class)
    public ResponseEntity<ApiResponse<Void>> handleInvalidStatementRequest(
            InvalidStatementRequestException ex,
            HttpServletRequest request) {

        log.warn("Invalid statement request: {}", ex.getMessage());

        return responseBuilder.badRequest(
                ex.getMessage(),
                request.getRequestURI()
        );
    }

    @ExceptionHandler(StatementEmailDeliveryException.class)
    public ResponseEntity<ApiResponse<Void>> handleStatementEmailDelivery(
            StatementEmailDeliveryException ex,
            HttpServletRequest request) {

        log.error("Bank statement email delivery failed", ex);

        return responseBuilder.error(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Unable to send bank statement email",
                request.getRequestURI()
        );
    }

    @ExceptionHandler(InsufficientBalanceException.class)
    public ResponseEntity<ApiResponse<Void>> handleInsufficientBalance(
            InsufficientBalanceException ex,
            HttpServletRequest request) {

        log.warn("Insufficient balance: {}", ex.getMessage());

        return responseBuilder.badRequest(
                ex.getMessage(),
                request.getRequestURI()
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationException(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        FieldError fieldError = ex.getBindingResult().getFieldError();

        String message = fieldError != null
                ? fieldError.getDefaultMessage()
                : "Validation failed";

        log.warn("Validation failed: {}", message);

        return responseBuilder.badRequest(
                message,
                request.getRequestURI()
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(
            Exception ex,
            HttpServletRequest request) {

        log.error("Unexpected exception occurred", ex);

        return responseBuilder.error(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Unexpected server error",
                request.getRequestURI()
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Void>> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex,
            HttpServletRequest request) {

        log.warn("Invalid request body: {}", ex.getMessage());

        return responseBuilder.badRequest(
                "Request body is missing or invalid",
                request.getRequestURI()
        );
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Void>> handleArgumentTypeMismatch(
            MethodArgumentTypeMismatchException ex,
            HttpServletRequest request) {

        log.warn("Invalid request parameter {}: {}", ex.getName(), ex.getValue());

        return responseBuilder.badRequest(
                "Invalid value for " + ex.getName(),
                request.getRequestURI()
        );
    }

    @ExceptionHandler(UnauthorizedAccessException.class)
    public ResponseEntity<ApiResponse<Void>> handleUnauthorizedAccess(
            UnauthorizedAccessException ex,
            HttpServletRequest request) {

        log.warn("Unauthorized access: {}", ex.getMessage());

        return responseBuilder.forbidden(
                ex.getMessage(),
                request.getRequestURI()
        );
    }

    @ExceptionHandler(org.springframework.security.authorization.AuthorizationDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handleAuthorizationDenied(
            AuthorizationDeniedException ex,
            HttpServletRequest request) {

        log.warn("Authorization denied: {}", ex.getMessage());

        return responseBuilder.forbidden(
                "You are not authorized to access this resource",
                request.getRequestURI()
        );
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationException(
            ValidationException ex,
            HttpServletRequest request) {

        log.warn("Validation error: {}", ex.getMessage());

        return responseBuilder.badRequest(
                ex.getMessage(),
                request.getRequestURI()
        );
    }

    @ExceptionHandler(InvalidInputException.class)
    public ResponseEntity<ApiResponse<Void>> handleInvalidInput(
            InvalidInputException ex,
            HttpServletRequest request) {

        log.warn("Invalid input: {}", ex.getMessage());

        return responseBuilder.badRequest(
                ex.getMessage(),
                request.getRequestURI()
        );
    }
}
