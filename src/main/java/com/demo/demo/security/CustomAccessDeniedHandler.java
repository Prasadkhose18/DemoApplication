package com.demo.demo.security;

import com.demo.demo.dto.response.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class CustomAccessDeniedHandler
        implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;



    public CustomAccessDeniedHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }


    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
             AccessDeniedException accessDeniedException)
            throws IOException, ServletException {

        log.warn(
                "Access denied. URI: {}",
                request.getRequestURI()
        );

        ApiResponse<Object> apiResponse =
                ApiResponse.error(
                        HttpStatus.FORBIDDEN,
                        "You are not authorized to access this resource.",
                        request.getRequestURI()
                );

        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json");

        objectMapper.writeValue(
                response.getOutputStream(),
                apiResponse
        );
    }
}