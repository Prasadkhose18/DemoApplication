package com.demo.demo.service;

import com.demo.demo.entity.User;
import com.demo.demo.exception.*;
import com.demo.demo.repository.UserRepository;
import com.demo.demo.security.CustomUserDetails;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

@Slf4j
@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User createUser(User user) {

        log.info("Creating new user with email: {}", user.getEmail());

        if (userRepository.existsByEmail(user.getEmail())) {
            log.warn("Registration failed. Email already exists: {}", user.getEmail());
            throw new DuplicateEmailException("Email already exists");
        }

        if (user.getMobile() != null &&
                userRepository.existsByMobile(user.getMobile())) {

            log.warn("Registration failed. Mobile number already exists: {}", user.getMobile());
            throw new DuplicateMobileNumberException("Mobile number already exists");
        }

        if (user.getPasswordHash() == null || user.getPasswordHash().isBlank()) {
            log.warn("Registration failed. Password is missing for user: {}", user.getEmail());
            throw new InvalidInputException("Password is required");
        }

        log.debug("Encoding password for user: {}", user.getEmail());
        user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));

        if (user.getRole() == null || user.getRole().isBlank()) {
            log.warn("Registration failed. Role is missing for user: {}", user.getEmail());
            throw new ValidationException("Role is required");
        }

        user.setRole(user.getRole().trim().toUpperCase());

        User savedUser = userRepository.save(user);

        log.info("User created successfully. User ID: {}, Email: {}",
                savedUser.getUserId(),
                savedUser.getEmail());

        return savedUser;
    }


    public User getUserById(Long userId) {

        log.info("Fetching user with ID: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("User not found with ID: {}", userId);
                    return new ResourceNotFoundException(
                            "User not found with ID: " + userId);
                });

        log.info("User found with ID: {}", userId);

        return user;
    }


    public Optional<User> getUserByEmail(String email) {

        log.debug("Fetching user by email: {}", email);

        return userRepository.findByEmail(email);
    }


    @Override
    public UserDetails loadUserByUsername(@NonNull String username)
            throws UsernameNotFoundException {

        log.debug("Loading UserDetails for username: {}", username);

        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> {
                    log.warn("Authentication failed. User not found: {}", username);
                    return new UsernameNotFoundException(
                            "User not found with email: " + username);
                });

        log.debug("UserDetails loaded successfully for: {}", username);

        return new CustomUserDetails(user);
    }
}