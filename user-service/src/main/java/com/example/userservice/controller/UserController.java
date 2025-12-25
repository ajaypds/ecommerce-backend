package com.example.userservice.controller;

import com.example.userservice.dto.AuthResponse;
import com.example.userservice.dto.LoginRequest;
import com.example.userservice.dto.RegisterRequest;
import com.example.userservice.dto.UserResponse;
import com.example.userservice.entity.User;
import com.example.userservice.security.JwtUtil;
import com.example.userservice.service.AuthService;
import com.example.userservice.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final AuthService authService;
    private final JwtUtil jwtUtil;


    @PostMapping("/register")
    public UserResponse register(@Valid @RequestBody RegisterRequest request) {
        User user = userService.register(request);
        return new UserResponse(
                user.getId().toString(),
                user.getEmail(),
                user.getRole()
        );
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest request) {

        return authService.login(
                request.getEmail(),
                request.getPassword()
        );
    }

    @GetMapping("/me")
    public UserResponse me(Authentication authentication) {

        String userId = (String) authentication.getPrincipal();
        User user = userService.me(userId);

        return new UserResponse(
                user.getId().toString(),
                user.getEmail(),
                user.getRole()
        );
    }


}

