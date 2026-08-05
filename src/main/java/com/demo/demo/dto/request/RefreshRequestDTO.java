package com.demo.demo.dto.request;

import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.NotBlank;

@Getter
@Setter
public class RefreshRequestDTO {

    @NotBlank(message = "Refresh token is required")
    private String refreshToken;
}
