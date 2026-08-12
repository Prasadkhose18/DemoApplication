package com.demo.demo.service.impl;

import com.demo.demo.dto.request.UpdateUserRequestDTO;
import com.demo.demo.entity.User;
import com.demo.demo.exception.*;
import com.demo.demo.repository.UserRepository;
import com.demo.demo.security.CustomUserDetails;
import com.demo.demo.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    @CachePut(value = "users", key = "#result.userId")
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


    @Cacheable(value = "users", key = "#userId")
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

    @Transactional
    @CachePut(value = "users", key = "#id")
    public User updateUser(Long id, UpdateUserRequestDTO updateDTO) {

        log.info("Updating user with ID: {}", id);

        User user = getUserById(id);

        if (updateDTO.getName() != null && !updateDTO.getName().isBlank()) {
            user.setName(updateDTO.getName().trim());
            log.debug("Updated name for user ID: {}", id);
        }

        if (updateDTO.getEmail() != null && !updateDTO.getEmail().isBlank()) {
            String newEmail = updateDTO.getEmail().trim();
            if (!newEmail.equals(user.getEmail()) && userRepository.existsByEmail(newEmail)) {
                log.warn("Email already exists: {}", newEmail);
                throw new DuplicateEmailException("Email is already in use");
            }
            user.setEmail(newEmail);
            log.debug("Updated email for user ID: {}", id);
        }

        if (updateDTO.getMobile() != null && !updateDTO.getMobile().isBlank()) {
            String newMobile = updateDTO.getMobile().trim();
            if (!newMobile.equals(user.getMobile()) && userRepository.existsByMobile(newMobile)) {
                log.warn("Mobile number already exists: {}", newMobile);
                throw new DuplicateMobileNumberException("Mobile number is already in use");
            }
            user.setMobile(newMobile);
            log.debug("Updated mobile for user ID: {}", id);
        }

        if (updateDTO.getPassword() != null && !updateDTO.getPassword().isBlank()) {
            if (updateDTO.getPassword().length() < 8) {
                log.warn("Password update failed for user ID: {} - Password too short", id);
                throw new InvalidInputException("Password must be at least 8 characters long");
            }
            user.setPasswordHash(passwordEncoder.encode(updateDTO.getPassword()));
            log.debug("Updated password for user ID: {}", id);
        }

        User updatedUser = userRepository.save(user);

        log.info("User updated successfully. User ID: {}", id);

        return updatedUser;
    }

    @Transactional
    @CacheEvict(value = "users", key = "#id")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteUser(Long id) {

        log.info("Attempting to delete user with ID: {}", id);

        if (!userRepository.existsById(id)) {
            log.warn("User not found for deletion. User ID: {}", id);
            throw new ResourceNotFoundException("User not found with ID: " + id);
        }

        userRepository.deleteById(id);

        log.info("User deleted successfully. User ID: {}", id);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public List<User> getAllUsers() {

        log.info("Fetching all users.");

        return userRepository.findAll();
    }


    public Optional<User> getUserByEmail(String email) {

        log.debug("Fetching user by email: {}", email);

        return userRepository.findByEmail(email);
    }


    @Override
    public UserDetails loadUserByUsername( String username)
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

