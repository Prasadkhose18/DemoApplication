package com.demo.demo.util;

import com.demo.demo.dto.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class APIResponseBuilder {

    public <T> ResponseEntity<ApiResponse<T>> success(
            HttpStatus status,
            String message,
            T data,
            String path) {

        return ResponseEntity.status(status)
                .body(ApiResponse.success(
                        status,
                        message,
                        data,
                        path
                ));
    }

    public <T> ResponseEntity<ApiResponse<T>> ok(
            String message,
            T data,
            String path) {

        return success(
                HttpStatus.OK,
                message,
                data,
                path
        );
    }

    public <T> ResponseEntity<ApiResponse<T>> created(
            String message,
            T data,
            String path) {

        return success(
                HttpStatus.CREATED,
                message,
                data,
                path
        );
    }

    public <T> ResponseEntity<ApiResponse<T>> accepted(
            String message,
            T data,
            String path) {

        return success(
                HttpStatus.ACCEPTED,
                message,
                data,
                path
        );
    }

    public <T> ResponseEntity<ApiResponse<T>> noContent(String message, String path) {
        ApiResponse<T> response = ApiResponse.success(
                HttpStatus.NO_CONTENT,
                message,
                null,
                path
        );
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
    }

    public <T> ResponseEntity<ApiResponse<T>> badRequest(
            String message,
            String path) {

        return error(
                HttpStatus.BAD_REQUEST,
                message,
                path
        );
    }

    public <T> ResponseEntity<ApiResponse<T>> notFound(
            String message,
            String path) {

        return error(
                HttpStatus.NOT_FOUND,
                message,
                path
        );
    }

    public <T> ResponseEntity<ApiResponse<T>> conflict(
            String message,
            String path) {

        return error(
                HttpStatus.CONFLICT,
                message,
                path
        );
    }

    public <T> ResponseEntity<ApiResponse<T>> forbidden(
            String message,
            String path) {

        return error(
                HttpStatus.FORBIDDEN,
                message,
                path
        );
    }

    public <T> ResponseEntity<ApiResponse<T>> error(
            HttpStatus status,
            String message,
            String path) {

        return ResponseEntity.status(status)
                .body(ApiResponse.error(
                        status,
                        message,
                        path
                ));
    }
}