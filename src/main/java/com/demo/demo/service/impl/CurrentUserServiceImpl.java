package com.demo.demo.service.impl;

import com.demo.demo.entity.User;
import com.demo.demo.exception.ResourceNotFoundException;
import com.demo.demo.repository.UserRepository;
import com.demo.demo.security.SecurityUtil;
import com.demo.demo.service.CurrentUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CurrentUserServiceImpl implements CurrentUserService {

    private final UserRepository userRepository;

    public CurrentUserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public String getEmail() {
        return SecurityUtil.getCurrentUserEmail();
    }


    public User getCurrentUser() {

        String email = getEmail();

        log.debug("Fetching current user: {}", email);

        return userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("Authenticated user not found: {}", email);
                    return new ResourceNotFoundException("Authenticated user not found");
                });
    }
}
