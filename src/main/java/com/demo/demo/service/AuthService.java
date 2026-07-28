package com.demo.demo.service;

import com.demo.demo.dto.response.AuthResponseDTO;
import com.demo.demo.dto.request.LoginRequestDTO;
import com.demo.demo.dto.request.RefreshRequestDTO;

public interface AuthService {

    AuthResponseDTO login(LoginRequestDTO request);

    AuthResponseDTO refreshToken(RefreshRequestDTO request);
}
