package com.demo.demo.security;

import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.UserDetails;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import java.util.Date;

@Service
public class JwtService {

    private static final String SECRETE_KEY = "jhgctghjkjnbvjiguhlkn";
    private static final long ACCESS_TOKEN_EXPIRATION = 15L * 60 * 1000; // 15 minutes
    private static final long REFRESH_TOKEN_EXPIRATION = 7L * 24 * 60 * 60 * 1000; // 7 days

    private Algorithm getAlgorithm() {
        return Algorithm.HMAC256(SECRETE_KEY);
    }

    public String generateAccessToken(UserDetails userDetails){
        return JWT.create()
                .withSubject(userDetails.getUsername())
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION))
                .sign(getAlgorithm());
    }

    public String generateRefreshToken(UserDetails userDetails){
        return JWT.create()
                .withSubject(userDetails.getUsername())
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION))
                .sign(getAlgorithm());
    }

    public String extractUsername(String token) {
        return JWT.require(getAlgorithm())
                .build()
                .verify(token)
                .getSubject();
    }

    public boolean isTokenValid(String token) {
        try {
            extractUsername(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}
