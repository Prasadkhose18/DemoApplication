package com.demo.demo.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Date;

@Slf4j
@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.access-expiration}")
    private long accessTokenExpiration;

    @Value("${jwt.refresh-expiration}")
    private long refreshTokenExpiration;

    private Algorithm getAlgorithm() {
        return Algorithm.HMAC256(secretKey);
    }

    public String generateAccessToken(UserDetails userDetails) {

        log.debug("Generating access token for user: {}", userDetails.getUsername());

        String token = JWT.create()
                .withSubject(userDetails.getUsername())
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + accessTokenExpiration))
                .sign(getAlgorithm());

        log.info("Access token generated successfully for user: {}", userDetails.getUsername());

        return token;
    }

    public String generateRefreshToken(UserDetails userDetails) {

        log.debug("Generating refresh token for user: {}", userDetails.getUsername());

        String token = JWT.create()
                .withSubject(userDetails.getUsername())
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + refreshTokenExpiration))
                .sign(getAlgorithm());

        log.info("Refresh token generated successfully for user: {}", userDetails.getUsername());

        return token;
    }

    public String extractUsername(String token) {

        log.debug("Extracting username from JWT.");

        String username = JWT.require(getAlgorithm())
                .build()
                .verify(token)
                .getSubject();

        log.debug("Username extracted successfully.");

        return username;
    }

    public boolean isTokenValid(String token) {

        log.debug("Validating JWT token.");

        try {
            extractUsername(token);

            log.debug("JWT token validation successful.");

            return true;

        } catch (Exception ex) {

            log.warn("JWT token validation failed: {}", ex.getMessage());

            return false;
        }
    }
}