package com.financeledger.financeledger.service;

import com.financeledger.financeledger.dto.request.LoginRequest;
import com.financeledger.financeledger.dto.request.RegisterRequest;
import com.financeledger.financeledger.dto.response.AuthResponse;
import com.financeledger.financeledger.entity.User;
import com.financeledger.financeledger.exception.AppException;
import com.financeledger.financeledger.exception.DuplicateResourceException;
import com.financeledger.financeledger.repository.UserRepository;
import com.financeledger.financeledger.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException(
                    "Email already registered: " + request.getEmail());
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(User.Role.USER)
                .build();

        userRepository.save(user);

        String token = jwtUtil.generateToken(
                user.getEmail(), user.getRole().name());

        return AuthResponse.builder()
                .token(token)
                .email(user.getEmail())
                .name(user.getName())
                .role(user.getRole().name())
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AppException(
                        "Invalid email or password",
                        org.springframework.http.HttpStatus.UNAUTHORIZED));

        if (!passwordEncoder.matches(
                request.getPassword(), user.getPasswordHash())) {
            throw new AppException(
                    "Invalid email or password",
                    org.springframework.http.HttpStatus.UNAUTHORIZED);
        }

        String token = jwtUtil.generateToken(
                user.getEmail(), user.getRole().name());

        return AuthResponse.builder()
                .token(token)
                .email(user.getEmail())
                .name(user.getName())
                .role(user.getRole().name())
                .build();
    }

}
