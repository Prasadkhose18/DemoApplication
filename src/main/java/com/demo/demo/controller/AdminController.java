package com.demo.demo.controller;

import com.demo.demo.dto.response.ApiResponse;
import com.demo.demo.entity.User;
import com.demo.demo.service.UserService;
import com.demo.demo.util.ApiResponseUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/admin")
@PreAuthorize("@currentUserService.isAdmin()")
public class AdminController {

    private final UserService userService;

    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users")
    public ResponseEntity<ApiResponse<List<User>>> getAllUsers(
            HttpServletRequest request) {

        log.info("Admin requested all users.");

        return ApiResponseUtil.success(
                userService.getAllUsers(),
                "Users fetched successfully",
                request.getRequestURI()
        );
    }
}