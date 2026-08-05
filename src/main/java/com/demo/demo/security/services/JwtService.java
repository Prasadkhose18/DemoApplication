package com.demo.demo.security.services;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.algorithms.Algorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

@Slf4j
@Service
public class JwtService {

    private static final String TOKEN_TYPE_CLAIM = "token_type";
    private static final String ACCESS_TOKEN_TYPE = "access";
    private static final String REFRESH_TOKEN_TYPE = "refresh";

    private final String secretKey;
    private final long accessTokenExpiration;
    private final long refreshTokenExpiration;
    private final String issuer;
    private final String audience;

    public JwtService(@Value("${jwt.secret}") String secretKey,
                      @Value("${jwt.access-expiration}") long accessTokenExpiration,
                      @Value("${jwt.refresh-expiration}") long refreshTokenExpiration,
                      @Value("${jwt.issuer}") String issuer,
                      @Value("${jwt.audience}") String audience) {
        this.secretKey = secretKey;
        this.accessTokenExpiration = accessTokenExpiration;
        this.refreshTokenExpiration = refreshTokenExpiration;
        this.issuer = issuer;
        this.audience = audience;
    }

    private Algorithm getAlgorithm() {
        return Algorithm.HMAC256(secretKey);
    }

    public String generateAccessToken(UserDetails userDetails) {

        log.debug("Generating access token for user: {}", userDetails.getUsername());

        String token = JWT.create()
                .withSubject(userDetails.getUsername())
                .withIssuer(issuer)
                .withAudience(audience)
                .withJWTId(UUID.randomUUID().toString())
                .withClaim(TOKEN_TYPE_CLAIM, ACCESS_TOKEN_TYPE)
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
                .withIssuer(issuer)
                .withAudience(audience)
                .withJWTId(UUID.randomUUID().toString())
                .withClaim(TOKEN_TYPE_CLAIM, REFRESH_TOKEN_TYPE)
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + refreshTokenExpiration))
                .sign(getAlgorithm());

        log.info("Refresh token generated successfully for user: {}", userDetails.getUsername());

        return token;
    }

    public String extractUsername(String token) {

        log.debug("Extracting username from JWT.");

        String username = verifyToken(token).getSubject();

        log.debug("Username '{}' extracted successfully.", username);

        return username;
    }

    public boolean isAccessTokenValid(String token) {

        return isTokenValidForType(token, ACCESS_TOKEN_TYPE);
    }

    public boolean isRefreshTokenValid(String token) {

        return isTokenValidForType(token, REFRESH_TOKEN_TYPE);
    }

    private boolean isTokenValidForType(String token, String expectedTokenType) {

        log.debug("Validating JWT token.");

        try {

            DecodedJWT jwt = verifyToken(token);
            String tokenType = jwt.getClaim(TOKEN_TYPE_CLAIM).asString();

            if (!expectedTokenType.equals(tokenType)
                    || jwt.getSubject() == null
                    || jwt.getSubject().isBlank()) {
                log.warn("JWT token validation failed: invalid token type or subject");
                return false;
            }

            log.debug("JWT token validation successful.");

            return true;

        } catch (Exception ex) {

            log.warn("JWT token validation failed: {}", ex.getMessage());

            return false;
        }
    }

    private DecodedJWT verifyToken(String token) {
        return JWT.require(getAlgorithm())
                .withIssuer(issuer)
                .withAudience(audience)
                .build()
                .verify(token);
    }
}
