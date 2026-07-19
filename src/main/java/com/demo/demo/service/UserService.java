package com.demo.demo.service;

import java.util.Optional;

import com.demo.demo.exception.DuplicateEmailException;
import com.demo.demo.exception.DuplicateMobileNumberException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.demo.demo.entity.User;
import com.demo.demo.repository.UserRepository;

@Service
public class UserService {

    private  final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Register a new user
     */
    @Transactional
    public User createUser(User user) {

        // Check if email already exists
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new DuplicateEmailException("Email already exists");
        }

        // Check if mobile already exists
        if (user.getMobile() != null &&
                userRepository.existsByMobile(user.getMobile())) {
            throw new DuplicateMobileNumberException("Mobile number already exists");
        }

        // Validate password
        if (user.getPasswordHash() == null || user.getPasswordHash().isBlank()) {
            throw new RuntimeException("Password is required");
        }

        // Encode password
        user.setPasswordHash(
                passwordEncoder.encode(user.getPasswordHash())
        );

        // Validate role
        if (user.getRole() == null || user.getRole().isBlank()) {
            throw new RuntimeException("Role is required");
        }

        // Normalize role
        user.setRole(user.getRole().trim().toUpperCase());

        return userRepository.save(user);
    }

    /**
     * Fetch user by ID
     */
    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() ->
                        new RuntimeException("User not found with ID: " + userId));
    }

    /**
     * Fetch user by email
     */
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}