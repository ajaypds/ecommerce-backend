package com.example.userservice.service;

import com.example.userservice.dto.RegisterRequest;
import com.example.userservice.entity.User;
import com.example.userservice.exception.BadRequestException;
import com.example.userservice.exception.UnauthorizedException;
import com.example.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User register(RegisterRequest request) {

        log.info("Registering user with email={}", request.getEmail());

        if (userRepository.existsByEmail(request.getEmail())) {
            log.warn("Registration failed - email already exists: {}", request.getEmail());
            throw new BadRequestException("Email already registered");
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role("USER")
                .createdAt(Instant.now())
                .build();

        userRepository.save(user);

        log.info("User registered successfully with id={}", user.getId());

        return user;
    }

    @Override
    public User authenticate(String email, String password) {

        log.info("Authenticating user with email={}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("Authentication failed - user not found: {}", email);
                    return new UnauthorizedException("Invalid credentials");
                });

        if (!passwordEncoder.matches(password, user.getPassword())) {
            log.warn("Authentication failed - invalid password for email={}", email);
            throw new UnauthorizedException("Invalid credentials");
        }

        log.info("User authenticated successfully id={}", user.getId());
        return user;
    }

    @Override
    public User me(String userId){
        User user = userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new UnauthorizedException("Invalid credentials"));
        return user;
    }
}

