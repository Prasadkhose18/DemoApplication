package com.demo.demo.security;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Objects;

public class SecurityUtil {

    private SecurityUtil() {}

    public static String getCurrentUserEmail() {
        Object principal =
                Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getPrincipal();

        if (principal instanceof UserDetails userDetails) {
            return userDetails.getUsername(); // email
        }

        assert principal != null;
        return principal.toString();
    }
}
