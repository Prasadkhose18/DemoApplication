package com.demo.demo.security;

import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.UserDetails;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import java.util.Date;

@Service
public class JwtService {

    private static final String SECRETE_KEY = "jhgctghjkjnbvjiguhlkn";

    public String generateToker(UserDetails userDetails){
        Algorithm algorithm = Algorithm.HMAC256(SECRETE_KEY);
        return JWT.create()
                .withSubject(userDetails.getUsername())
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + 24L * 60 * 60 * 1000))
                .sign(algorithm);
    }

    public String extractUsername(String token) {
        return JWT.require(Algorithm.HMAC256(SECRETE_KEY))
                .build()
                .verify(token)
                .getSubject();
    }

}
