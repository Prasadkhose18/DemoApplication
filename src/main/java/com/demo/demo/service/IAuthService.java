package com.demo.demo.service;

import com.demo.demo.dto.AuthResponseDTO;
import com.demo.demo.dto.LoginRequestDTO;
import com.demo.demo.dto.RefreshRequestDTO;

public interface IAuthService {

    AuthResponseDTO login(LoginRequestDTO request);

    AuthResponseDTO refreshToken(RefreshRequestDTO request);
}
