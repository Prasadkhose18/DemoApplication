package com.demo.demo.security;

import com.demo.demo.config.SecurityProperties;
import com.demo.demo.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
public class PreAuthFilter extends OncePerRequestFilter {

    private final UserService userService;
    private final SecurityProperties securityProperties;

    public PreAuthFilter(UserService userService,
                         SecurityProperties securityProperties) {
        this.userService = userService;
        this.securityProperties = securityProperties;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        if (securityProperties.getMode() != AuthMode.PREAUTH) {
            filterChain.doFilter(request, response);
            return;
        }

        String preAuthKey =
                request.getHeader(SecurityConstants.PRE_AUTH_KEY_HEADER);

        if (preAuthKey == null || preAuthKey.isBlank()) {

            log.warn("Missing Pre-Auth key.");

            response.sendError(
                    HttpStatus.UNAUTHORIZED.value(),
                    "Missing Pre-Auth Key");

            return;
        }

        if (!preAuthKey.equals(securityProperties.getPreAuthKey())) {

            log.warn("Invalid Pre-Auth key received.");

            response.sendError(
                    HttpStatus.UNAUTHORIZED.value(),
                    "Invalid Pre-Auth Key");

            return;
        }

        String email =
                request.getHeader(SecurityConstants.PRE_AUTH_HEADER);

        if (email == null || email.isBlank()) {

            log.warn("Missing user email header.");

            response.sendError(
                    HttpStatus.UNAUTHORIZED.value(),
                    "Missing User Email");

            return;
        }

        if (SecurityContextHolder.getContext().getAuthentication() == null) {

            UserDetails userDetails =
                    userService.loadUserByUsername(email);

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities());

            SecurityContextHolder.getContext()
                    .setAuthentication(authentication);

            log.info("Pre-authentication successful for {}", email);
        }

        filterChain.doFilter(request, response);
    }
}