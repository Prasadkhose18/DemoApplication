package com.demo.demo.dto;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserResponseDTO {

    private long userId;
    private String name;
    private String email;
    private String mobile;
    private String role;
}
