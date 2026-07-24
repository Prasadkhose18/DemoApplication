package com.demo.demo.util;

import com.demo.demo.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class ResponseBuilder {

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
}