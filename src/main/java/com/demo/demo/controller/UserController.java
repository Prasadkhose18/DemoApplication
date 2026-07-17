package com.demo.demo.controller;


import com.demo.demo.dto.UserResponseDTO;
import com.demo.demo.entity.User;
import com.demo.demo.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }


    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Long id){
        User user = UserService.getByUserId(id);

        UserResponseDTO responseDTO = new UserResponseDTO();
        assert user != null;
        responseDTO.setUserId(user.getUserId());
        responseDTO.setName(user.getName());
        responseDTO.setEmail(user.getEmail());
        responseDTO.setMobile(user.getMobile());
        responseDTO.setRole(user.getRole());

        return ResponseEntity.ok(responseDTO);

    }

}
