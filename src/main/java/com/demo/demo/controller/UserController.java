package com.demo.demo.controller;

import com.demo.demo.dto.ApiResponse;
import com.demo.demo.dto.UserRequestDTO;
import com.demo.demo.dto.UserResponseDTO;
import com.demo.demo.entity.User;
import com.demo.demo.mapper.UserMapper;
import com.demo.demo.service.UserService;
import com.demo.demo.util.ResponseBuilder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;
    private final ResponseBuilder responseBuilder;

    public UserController(UserService userService,
                          UserMapper userMapper,
                          ResponseBuilder responseBuilder) {
        this.userService = userService;
        this.userMapper = userMapper;
        this.responseBuilder = responseBuilder;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponseDTO>> getUserById(
            @PathVariable Long id,
            HttpServletRequest request) {

        log.info("GET /users/{} request received", id);

        User user = userService.getUserById(id);

        log.info("Returning user with ID: {}", id);

        return responseBuilder.ok(
                "User fetched successfully",
                userMapper.toResponseDTO(user),
                request.getRequestURI()
        );
    }

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<UserResponseDTO>> createUser(
            @Valid @RequestBody UserRequestDTO requestDTO,
            HttpServletRequest request) {

        log.info("User registration request received for {}", requestDTO.getEmail());

        User user = userMapper.toEntity(requestDTO);

        User savedUser = userService.createUser(user);

        log.info("User created successfully with ID {}", savedUser.getUserId());

        return responseBuilder.created(
                "User created successfully",
                userMapper.toResponseDTO(savedUser),
                request.getRequestURI()
        );
    }
}