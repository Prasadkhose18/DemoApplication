package com.demo.demo.mapper;


import com.demo.demo.dto.UserRequestDTO;
import com.demo.demo.dto.UserResponseDTO;
import com.demo.demo.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public User toEntity(UserRequestDTO requestDTO){
        User user = new User();
        user.setName(requestDTO.getName());
        user.setEmail(requestDTO.getEmail());
        user.setMobile(requestDTO.getMobile());
        user.setPasswordHash(requestDTO.getPassword());
        user.setRole(requestDTO.getRole());

        return user;
    }

    public UserResponseDTO toResponseDTO(User user) {

        UserResponseDTO dto = new UserResponseDTO();

        dto.setUserId(user.getUserId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setMobile(user.getMobile());
        dto.setRole(user.getRole());

        return dto;
    }
}
