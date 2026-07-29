package com.demo.demo.service;

import com.demo.demo.entity.User;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;
import java.util.Optional;

public interface UserService extends UserDetailsService {

    User createUser(User user);

    User getUserById(Long id);

    Optional<User> getUserByEmail(String email);

    List<User> getAllUsers();
}