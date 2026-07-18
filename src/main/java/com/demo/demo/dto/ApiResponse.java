package com.demo.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class ApiResponse<T> {

    private LocalDateTime timestamp;
    private int status;
    private String message;
    private T data;
    private String path;
    public static <T> ApiResponse<T> success(
            HttpStatus status,
            String message,
            T data,
            String path
    ) {
        return new ApiResponse<>(
                LocalDateTime.now(),
                status.value(),
                message,
                data,
                path
        );
    }
}
