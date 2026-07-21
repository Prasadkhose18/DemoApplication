package com.demo.demo.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
public class ErrorResponseDTO {

    private LocalDateTime timestamp;

    private int status;
    private String error;
    private String path;
    private String message;
}