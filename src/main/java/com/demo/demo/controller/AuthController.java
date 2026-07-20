package com.demo.demo.controller;

import com.demo.demo.dto.LoginRequestDTO;
import com.demo.demo.dto.AuthResponseDTO;
import com.demo.demo.service.UserService;
import com.demo.demo.security.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.password.PasswordEncoder;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UserService userService, JwtService jwtService, PasswordEncoder passwordEncoder){
        this.userService = userService;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody LoginRequestDTO req){
        var userOpt = userService.getUserByEmail(req.getEmail());
        if (userOpt.isEmpty() || !passwordEncoder.matches(req.getPassword(), userOpt.get().getPasswordHash())) {
            return ResponseEntity.status(401).build();
        }
        var userDetails = new com.demo.demo.security.CustomUserDetails(userOpt.get());
        String token = jwtService.generateToker(userDetails);
        return ResponseEntity.ok(new AuthResponseDTO(token));
    }
}
