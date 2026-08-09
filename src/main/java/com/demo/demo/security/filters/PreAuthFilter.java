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
            return;
        }

        // Extract Pre-Auth headers
        String userEmail = request.getHeader(SecurityConstants.PRE_AUTH_HEADER);
        String preAuthKey = request.getHeader(SecurityConstants.PRE_AUTH_KEY_HEADER);

        log.debug("Pre-Auth attempt for email: {}", userEmail);

        if (userEmail == null || userEmail.isBlank() || preAuthKey == null || preAuthKey.isBlank()) {
            log.warn("Missing Pre-Auth headers");
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.getWriter().write("Missing Pre-Auth headers");
            return;
        }

        // Validate pre-auth key
        if (!preAuthKey.equals(securityProperties.getPreAuthKey())) {
            log.warn("Invalid Pre-Auth key for email: {}", userEmail);
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.getWriter().write("Invalid Pre-Auth key");
            return;
        }

        // Load user details
        UserDetails userDetails = userService.loadUserByUsername(userEmail);
        log.info("User authenticated via Pre-Auth: {}", userEmail);

        // Create authentication token
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());

        // Set authentication in security context
        SecurityContextHolder.getContext().setAuthentication(authToken);

        // Continue filter chain
        filterChain.doFilter(request, response);
    }
}