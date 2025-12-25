package com.example.userservice.service;

import com.example.userservice.dto.AuthResponse;
import com.example.userservice.entity.RefreshToken;
import com.example.userservice.entity.User;
import com.example.userservice.exception.UnauthorizedException;
import com.example.userservice.repository.RefreshTokenRepository;
import com.example.userservice.security.JwtUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AuthServiceImpl implements AuthService {

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    public AuthResponse login(String email, String password) {

        User user = userService.authenticate(email, password);

        log.info("Deleting existing refresh tokens for user id={}", user.getId());
        refreshTokenRepository.deleteByUser(user);

        String accessToken = jwtUtil.generateAccessToken(user);

        RefreshToken refreshToken = RefreshToken.builder()
                .token(UUID.randomUUID().toString())
                .user(user)
                .expiryDate(Instant.now().plus(30, ChronoUnit.DAYS))
                .build();

        refreshTokenRepository.save(refreshToken);

        log.info("Refresh token created for user id={}", user.getId());

        return new AuthResponse(accessToken, refreshToken.getToken());
    }

    @Override
    public AuthResponse refresh(String token) {

        RefreshToken refreshToken = refreshTokenRepository
                .findByToken(token)
                .orElseThrow(() -> new UnauthorizedException("Invalid refresh token"));

        if (refreshToken.getExpiryDate().isBefore(Instant.now())) {
            throw new UnauthorizedException("Refresh token expired");
        }

        User user = refreshToken.getUser();
        String accessToken = jwtUtil.generateAccessToken(user);

        return new AuthResponse(accessToken, refreshToken.getToken());
    }

    @Override
    public void logout(String token) {
        refreshTokenRepository.findByToken(token)
                .ifPresent(refreshTokenRepository::delete);
    }
}

