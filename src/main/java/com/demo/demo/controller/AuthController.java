package com.demo.demo.controller;

import com.demo.demo.dto.ApiResponse;
import com.demo.demo.dto.AuthResponseDTO;
import com.demo.demo.dto.LoginRequestDTO;
import com.demo.demo.dto.RefreshRequestDTO;
import com.demo.demo.service.AuthService;
import com.demo.demo.util.ResponseBuilder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final ResponseBuilder responseBuilder;

    public AuthController(AuthService authService,
                          ResponseBuilder responseBuilder) {
        this.authService = authService;
        this.responseBuilder = responseBuilder;
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponseDTO>> login(
            @Valid @RequestBody LoginRequestDTO request,
            HttpServletRequest httpRequest) {

        log.info("Login request received for email: {}", request.getEmail());

        AuthResponseDTO response = authService.login(request);

        log.info("Login successful for email: {}", request.getEmail());

        return responseBuilder.ok(
                "Login successful",
                response,
                httpRequest.getRequestURI()
        );
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponseDTO>> refresh(
            @Valid @RequestBody RefreshRequestDTO request,
            HttpServletRequest httpRequest) {

        log.info("Refresh token request received.");

        AuthResponseDTO response = authService.refreshToken(request);

        log.info("Access token refreshed successfully.");

        return responseBuilder.ok(
                "Access token refreshed successfully",
                response,
                httpRequest.getRequestURI()
        );
    }
}