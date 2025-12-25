package com.example.userservice.service;

import com.example.userservice.dto.AuthResponse;

public interface AuthService {

    AuthResponse login(String email, String password);

    AuthResponse refresh(String refreshToken);

    void logout(String refreshToken);
}

