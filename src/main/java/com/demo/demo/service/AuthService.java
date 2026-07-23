package com.demo.demo.service;

import com.demo.demo.dto.AuthResponseDTO;
import com.demo.demo.dto.LoginRequestDTO;
import com.demo.demo.dto.RefreshRequestDTO;
import com.demo.demo.entity.User;
import com.demo.demo.exception.InvalidCredentialsException;
import com.demo.demo.security.CustomUserDetails;
import com.demo.demo.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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

        User user = userService.getUserByEmail(request.getEmail())
                .orElseThrow(() ->
                        new InvalidCredentialsException("Invalid email or password"));

        if (!passwordEncoder.matches(
                request.getPassword(),
                user.getPasswordHash())) {

            throw new InvalidCredentialsException("Invalid email or password");
        }

        return buildTokenResponse(user);
    }

    public AuthResponseDTO refreshToken(RefreshRequestDTO request) {

        String username = jwtService.extractUsername(request.getRefreshToken());

        User user = userService.getUserByEmail(username)
                .orElseThrow(() ->
                        new InvalidCredentialsException("Invalid refresh token"));

        return buildAccessTokenResponse(user, request.getRefreshToken());
    }

    private AuthResponseDTO buildAccessTokenResponse(User user, String refreshToken) {

        CustomUserDetails userDetails = new CustomUserDetails(user);

        String accessToken = jwtService.generateAccessToken(userDetails);

        return new AuthResponseDTO(
                accessToken,
                refreshToken
        );
    }

    private AuthResponseDTO buildTokenResponse(User user) {

        CustomUserDetails userDetails = new CustomUserDetails(user);

        String accessToken = jwtService.generateAccessToken(userDetails);
        String refreshToken = jwtService.generateRefreshToken(userDetails);

        return new AuthResponseDTO(
                accessToken,
                refreshToken
        );
    }
}