package com.financeledger.financeledger.service;

import com.financeledger.financeledger.dto.request.LoginRequest;
import com.financeledger.financeledger.dto.request.RegisterRequest;
import com.financeledger.financeledger.dto.response.AuthResponse;
import com.financeledger.financeledger.entity.User;
import com.financeledger.financeledger.exception.AppException;
import com.financeledger.financeledger.exception.DuplicateResourceException;
import com.financeledger.financeledger.repository.UserRepository;
import com.financeledger.financeledger.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    UserRepository userRepository;
    @Mock
    PasswordEncoder passwordEncoder;
    @Mock
    JwtUtil jwtUtil;

    @InjectMocks
    AuthService authService;

    @Test
    @DisplayName("Should register user successfully")
    void shouldRegisterUserSuccessfully() {
        RegisterRequest request = new RegisterRequest(
                "John Doe", "john@test.com", "password123");

        when(userRepository.existsByEmail(anyString()))
                .thenReturn(false);
        when(passwordEncoder.encode(anyString()))
                .thenReturn("hashedPassword");
        when(userRepository.save(any(User.class)))
                .thenAnswer(i -> i.getArgument(0));
        when(jwtUtil.generateToken(anyString(), anyString()))
                .thenReturn("mock.jwt.token");

        AuthResponse response = authService.register(request);

        assertThat(response.getEmail()).isEqualTo("john@test.com");
        assertThat(response.getName()).isEqualTo("John Doe");
        assertThat(response.getToken()).isEqualTo("mock.jwt.token");
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw exception when email already exists")
    void shouldThrowWhenEmailAlreadyExists() {
        RegisterRequest request = new RegisterRequest(
                "John Doe", "john@test.com", "password123");

        when(userRepository.existsByEmail("john@test.com"))
                .thenReturn(true);

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("Email already registered");
    }

    @Test
    @DisplayName("Should login successfully with correct credentials")
    void shouldLoginSuccessfully() {
        LoginRequest request = new LoginRequest(
                "john@test.com", "password123");

        User user = User.builder()
                .name("John Doe")
                .email("john@test.com")
                .passwordHash("hashedPassword")
                .role(User.Role.USER)
                .build();

        when(userRepository.findByEmail("john@test.com"))
                .thenReturn(Optional.of(user));
        when(passwordEncoder.matches(anyString(), anyString()))
                .thenReturn(true);
        when(jwtUtil.generateToken(anyString(), anyString()))
                .thenReturn("mock.jwt.token");

        AuthResponse response = authService.login(request);

        assertThat(response.getToken()).isEqualTo("mock.jwt.token");
        assertThat(response.getEmail()).isEqualTo("john@test.com");
    }

    @Test
    @DisplayName("Should throw exception on wrong password")
    void shouldThrowOnWrongPassword() {
        LoginRequest request = new LoginRequest(
                "john@test.com", "wrongpassword");

        User user = User.builder()
                .email("john@test.com")
                .passwordHash("hashedPassword")
                .role(User.Role.USER)
                .build();

        when(userRepository.findByEmail("john@test.com"))
                .thenReturn(Optional.of(user));
        when(passwordEncoder.matches(anyString(), anyString()))
                .thenReturn(false);

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(AppException.class)
                .hasMessageContaining("Invalid email or password");
    }
}