package com.demo.demo.util;

import com.demo.demo.dto.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public final class ApiResponseUtil {

    private ApiResponseUtil() {
    }

    public static <T> ResponseEntity<ApiResponse<T>> success(
            T data,
            String message,
            String path) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        HttpStatus.OK,
                        message,
                        data,
                        path
                )
        );
    }

    public static <T> ResponseEntity<ApiResponse<T>> created(
            T data,
            String message,
            String path) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(
                        ApiResponse.success(
                                HttpStatus.CREATED,
                                message,
                                data,
                                path
                        )
                );
    }
}