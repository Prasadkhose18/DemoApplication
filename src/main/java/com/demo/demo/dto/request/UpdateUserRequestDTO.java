package com.demo.demo.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateUserRequestDTO {

    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;

    @Email(message = "Email must be valid")
    private String email;

    @Size(min = 10, max = 15, message = "Mobile must be between 10 and 15 characters")
    private String mobile;

    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;
}
