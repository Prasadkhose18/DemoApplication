package com.demo.demo.controller;


import com.demo.demo.dto.UserRequestDTO;
import com.demo.demo.dto.UserResponseDTO;
import com.demo.demo.entity.User;
import com.demo.demo.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.swing.text.StyledEditorKit;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }


    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Long id){

        User user = userService.getUserById(id);

        UserResponseDTO responseDTO = new UserResponseDTO();
        assert user != null;
        responseDTO.setUserId(user.getUserId());
        responseDTO.setName(user.getName());
        responseDTO.setEmail(user.getEmail());
        responseDTO.setMobile(user.getMobile());
        responseDTO.setRole(user.getRole());

        return ResponseEntity.ok(responseDTO);



    }

    @PostMapping("/create")
    public ResponseEntity<UserResponseDTO> createUser
            (@RequestBody UserRequestDTO requestDTO){

        // DTO -> Entity
        User user = new User();
        user.setName(requestDTO.getName());
        user.setEmail(requestDTO.getEmail());
        user.setMobile(requestDTO.getMobile());
        user.setPasswordHash(requestDTO.getPassword());
        user.setRole(requestDTO.getRole());

        User savedUser = userService.createUser(user);

        // Entity -> DTO

        UserResponseDTO responseDTO = new UserResponseDTO();
        responseDTO.setUserId(savedUser.getUserId());
        responseDTO.setName(savedUser.getName());
        responseDTO.setEmail(savedUser.getEmail());
        responseDTO.setMobile(savedUser.getMobile());
        responseDTO.setRole(savedUser.getRole());

        return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
    }

}
