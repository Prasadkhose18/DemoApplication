package com.demo.demo.security.filters;

import com.demo.demo.config.SecurityProperties;
import com.demo.demo.security.AuthMode;
import com.demo.demo.security.SecurityConstants;
import com.demo.demo.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
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
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        if (securityProperties.getMode() != AuthMode.PREAUTH) {
            filterChain.doFilter(request, response);
            return;
        }

        String path = request.getServletPath();

        if (path.startsWith("/auth/")
                || path.equals("/users/create")
                || path.startsWith("/swagger-ui")
                || path.equals("/swagger-ui.html")
                || path.startsWith("/v3/api-docs")
                || path.startsWith("/api-docs")) {

            filterChain.doFilter(request, response);

        }

        // Existing Pre-Auth logic...
    }
}