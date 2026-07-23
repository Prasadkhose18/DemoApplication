package com.demo.demo.controller;

import com.demo.demo.dto.AuthResponseDTO;
import com.demo.demo.dto.LoginRequestDTO;
import com.demo.demo.dto.RefreshRequestDTO;
import com.demo.demo.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(
            @RequestBody LoginRequestDTO request) {

        return ResponseEntity.ok(
                authService.login(request)
        );
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponseDTO> refresh(
            @RequestBody RefreshRequestDTO request) {

        return ResponseEntity.ok(
                authService.refreshToken(request)
        );
    }
}