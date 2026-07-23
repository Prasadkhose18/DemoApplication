package com.demo.demo.service;

import com.demo.demo.dto.AuthResponseDTO;
import com.demo.demo.dto.LoginRequestDTO;
import com.demo.demo.dto.RefreshRequestDTO;
import com.demo.demo.entity.User;
import com.demo.demo.exception.InvalidCredentialsException;
import com.demo.demo.security.CustomUserDetails;
import com.demo.demo.security.JwtService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AuthService {

    private final UserService userService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserService userService,
                       JwtService jwtService,
                       PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }


    public AuthResponseDTO login(LoginRequestDTO request) {

        log.info("Login request received for email: {}", request.getEmail());

        User user = userService.getUserByEmail(request.getEmail())
                .orElseThrow(() -> {
                    log.warn("Login failed. User not found: {}", request.getEmail());
                    return new InvalidCredentialsException("Invalid email or password");
                });

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {

            log.warn("Login failed. Invalid password for: {}", request.getEmail());

            throw new InvalidCredentialsException("Invalid email or password");
        }

        log.info("Login successful for user: {}", user.getEmail());

        return buildTokenResponse(user);
    }


    public AuthResponseDTO refreshToken(RefreshRequestDTO request) {

        log.info("Refresh token request received.");

        if (!jwtService.isTokenValid(request.getRefreshToken())) {

            log.warn("Invalid refresh token.");

            throw new InvalidCredentialsException("Invalid refresh token");
        }

        String username = jwtService.extractUsername(request.getRefreshToken());

        User user = userService.getUserByEmail(username)
                .orElseThrow(() -> {
                    log.warn("Refresh token belongs to unknown user: {}", username);
                    return new InvalidCredentialsException("Invalid refresh token");
                });

        log.info("Access token regenerated successfully for {}", username);

        return buildTokenResponse(user, request.getRefreshToken());
    }


    private AuthResponseDTO buildTokenResponse(User user) {

        CustomUserDetails userDetails = new CustomUserDetails(user);

        return new AuthResponseDTO(
                jwtService.generateAccessToken(userDetails),
                jwtService.generateRefreshToken(userDetails)
        );
    }

    private AuthResponseDTO buildTokenResponse(User user,
                                               String refreshToken) {

        CustomUserDetails userDetails = new CustomUserDetails(user);

        return new AuthResponseDTO(
                jwtService.generateAccessToken(userDetails),
                refreshToken
        );
    }
}