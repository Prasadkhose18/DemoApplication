package com.demo.demo.service;

import com.demo.demo.dto.request.UpdateUserRequestDTO;
import com.demo.demo.entity.User;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;
import java.util.Optional;

public interface UserService extends UserDetailsService {

    User createUser(User user);

    User getUserById(Long id);

    User updateUser(Long id, UpdateUserRequestDTO updateDTO);

    void deleteUser(Long id);

    Optional<User> getUserByEmail(String email);

    List<User> getAllUsers();
}