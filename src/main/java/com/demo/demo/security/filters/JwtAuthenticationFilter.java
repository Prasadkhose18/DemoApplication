package com.demo.demo.security.filters;

import com.demo.demo.config.SecurityProperties;
import com.demo.demo.security.AuthMode;
import com.demo.demo.security.services.JwtService;
import com.demo.demo.security.SecurityConstants;
import com.demo.demo.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserService userService;
    private final SecurityProperties securityProperties;

    public JwtAuthenticationFilter(
            JwtService jwtService,
            UserService userService,
            SecurityProperties securityProperties) {

        this.jwtService = jwtService;
        this.userService = userService;
        this.securityProperties = securityProperties;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
             HttpServletResponse response,
             FilterChain filterChain)
            throws ServletException, IOException {

        if (securityProperties.getMode() != AuthMode.JWT) {
            filterChain.doFilter(request, response);
            return;
        }

        log.debug("Processing request {}", request.getRequestURI());

        String authHeader =
                request.getHeader(SecurityConstants.AUTHORIZATION_HEADER);

        if (authHeader == null ||
                !authHeader.startsWith(SecurityConstants.BEARER_PREFIX)) {

            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(
                SecurityConstants.BEARER_PREFIX.length());

        if (!jwtService.isTokenValid(token)) {

            log.warn("Invalid JWT token");

            filterChain.doFilter(request, response);
            return;
        }

        String username = jwtService.extractUsername(token);

        if (SecurityContextHolder.getContext().getAuthentication() == null) {

            UserDetails userDetails =
                    userService.loadUserByUsername(username);

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities());

            SecurityContextHolder.getContext()
                    .setAuthentication(authentication);

            log.info("JWT authentication successful for {}", username);
        }

        filterChain.doFilter(request, response);
    }
}