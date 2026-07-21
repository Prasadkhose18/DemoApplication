package com.demo.demo.controller;
import com.demo.demo.dto.UserRequestDTO;
import com.demo.demo.dto.UserResponseDTO;
import com.demo.demo.entity.User;
import com.demo.demo.mapper.UserMapper;
import com.demo.demo.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    public UserController(UserService userService,
                          UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Long id){

        User user = userService.getUserById(id);
        return ResponseEntity.ok(userMapper.toResponseDTO(user));
    }

    @PostMapping("/create")
    public ResponseEntity<UserResponseDTO> createUser
            (@Valid @RequestBody UserRequestDTO requestDTO){

        // DTO -> Entity
        User user = userMapper.toEntity(requestDTO);

        User savedUser = userService.createUser(user);

        // Entity -> DTO
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userMapper.toResponseDTO(savedUser));
    }

}
