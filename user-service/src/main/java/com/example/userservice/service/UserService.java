package com.example.userservice.service;

import com.example.userservice.dto.RegisterRequest;
import com.example.userservice.entity.User;

public interface UserService {

    User register(RegisterRequest request);

    User authenticate(String email, String password);

    User me(String userId);
}
