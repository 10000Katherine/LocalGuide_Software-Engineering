package com.localguide.modules.authuser.service;

import com.localguide.common.exception.ApiException;
import com.localguide.common.security.JwtService;
import com.localguide.modules.authuser.domain.User;
import com.localguide.modules.authuser.dto.AuthResponse;
import com.localguide.modules.authuser.dto.LoginRequest;
import com.localguide.modules.authuser.dto.RefreshTokenRequest;
import com.localguide.modules.authuser.dto.RegisterRequest;
import com.localguide.modules.authuser.dto.ResetPasswordRequest;
import com.localguide.modules.authuser.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    private AuthService authService;

    @BeforeEach
    void setUp() {
        authService = new AuthService(userRepository, passwordEncoder, jwtService);
    }

    @Test
    void registerShouldNormalizeEmailAndIssueTokens() {
        RegisterRequest request = new RegisterRequest("  Test@Example.com  ", "Password123!", "Tour", "User", "+1");

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("Password123!")).thenReturn("HASHED");
        when(jwtService.generateAccessToken("test@example.com")).thenReturn("ACCESS");
        when(jwtService.generateRefreshToken("test@example.com")).thenReturn("REFRESH");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AuthResponse response = authService.register(request);

        assertEquals("ACCESS", response.accessToken());
        assertEquals("REFRESH", response.refreshToken());
        assertEquals("test@example.com", response.user().email());

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository, atLeast(2)).save(userCaptor.capture());
        assertTrue(userCaptor.getAllValues().stream().anyMatch(saved -> "test@example.com".equals(saved.getEmail())));
        assertTrue(userCaptor.getAllValues().stream().anyMatch(saved -> "HASHED".equals(saved.getPasswordHash())));
        assertTrue(userCaptor.getAllValues().stream().anyMatch(saved -> saved.getRefreshTokenHash() != null));
    }

    @Test
    void registerShouldThrowConflictWhenEmailExists() {
        RegisterRequest request = new RegisterRequest("test@example.com", "Password123!", "Tour", "User", null);
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(new User()));

        ApiException exception = assertThrows(ApiException.class, () -> authService.register(request));
        assertEquals(HttpStatus.CONFLICT, exception.getStatus());
    }

    @Test
    void loginShouldThrowUnauthorizedWhenPasswordDoesNotMatch() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setPasswordHash("HASHED");
        user.setActive(true);

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", "HASHED")).thenReturn(false);

        ApiException exception = assertThrows(ApiException.class,
                () -> authService.login(new LoginRequest("test@example.com", "wrong")));
        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatus());
    }

    @Test
    void refreshShouldThrowUnauthorizedWhenTokenInvalid() {
        when(jwtService.isTokenValid("bad-token")).thenReturn(false);

        ApiException exception = assertThrows(ApiException.class,
                () -> authService.refresh(new RefreshTokenRequest("bad-token")));
        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatus());
    }

    @Test
    void requestResetShouldReturnGenericMessageForUnknownEmail() {
        when(userRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        String message = authService.requestResetPassword(new ResetPasswordRequest("unknown@example.com")).message();

        assertTrue(message.contains("If the account exists"));
    }
}
