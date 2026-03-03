package com.localguide.modules.authuser.service;

import com.localguide.common.exception.ApiException;
import com.localguide.common.security.JwtService;
import com.localguide.modules.authuser.domain.User;
import com.localguide.modules.authuser.domain.UserRole;
import com.localguide.modules.authuser.dto.ApiMessageResponse;
import com.localguide.modules.authuser.dto.AuthResponse;
import com.localguide.modules.authuser.dto.ConfirmResetPasswordRequest;
import com.localguide.modules.authuser.dto.LoginRequest;
import com.localguide.modules.authuser.dto.RefreshTokenRequest;
import com.localguide.modules.authuser.dto.RegisterRequest;
import com.localguide.modules.authuser.dto.ResetPasswordRequest;
import com.localguide.modules.authuser.dto.UserResponse;
import com.localguide.modules.authuser.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AuthService {
    private static final long RESET_TOKEN_EXPIRATION_SECONDS = 900;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final Map<String, ResetTokenRecord> resetTokenIndex = new ConcurrentHashMap<>();

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public AuthResponse register(RegisterRequest request) {
        String normalizedEmail = normalizeEmail(request.email());
        userRepository.findByEmail(normalizedEmail).ifPresent(existing -> {
            throw new ApiException(HttpStatus.CONFLICT, "Email already registered");
        });

        User user = new User();
        user.setEmail(normalizedEmail);
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setPhone(request.phone());
        user.setRole(UserRole.TOURIST);

        User saved = userRepository.save(user);
        return issueTokens(saved);
    }

    public AuthResponse login(LoginRequest request) {
        String normalizedEmail = normalizeEmail(request.email());
        User user = userRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Invalid email or password"));

        if (!user.isActive() || !passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
        }

        return issueTokens(user);
    }

    public AuthResponse refresh(RefreshTokenRequest request) {
        String token = request.refreshToken();
        if (!jwtService.isTokenValid(token) || !"refresh".equals(jwtService.extractType(token))) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Invalid refresh token");
        }

        String email = jwtService.extractSubject(token);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "User not found"));

        if (!hashToken(token).equals(user.getRefreshTokenHash())) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Refresh token revoked");
        }

        return issueTokens(user);
    }

    public ApiMessageResponse requestResetPassword(ResetPasswordRequest request) {
        String normalizedEmail = normalizeEmail(request.email());
        userRepository.findByEmail(normalizedEmail)
                .ifPresent(user -> {
                    String resetToken = generateResetToken();
                    String tokenHash = hashToken(resetToken);
                    resetTokenIndex.put(tokenHash, new ResetTokenRecord(user.getEmail(), Instant.now().plusSeconds(RESET_TOKEN_EXPIRATION_SECONDS)));
                });

        purgeExpiredResetTokens();
        return new ApiMessageResponse("If the account exists, reset instructions will be sent");
    }

    public ApiMessageResponse confirmResetPassword(ConfirmResetPasswordRequest request) {
        purgeExpiredResetTokens();
        String providedTokenHash = hashToken(request.resetToken());
        ResetTokenRecord tokenRecord = resetTokenIndex.remove(providedTokenHash);
        if (tokenRecord == null || tokenRecord.expiresAt().isBefore(Instant.now())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Reset token is invalid or expired");
        }

        User user = userRepository.findByEmail(tokenRecord.email())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found"));

        user.setPasswordHash(passwordEncoder.encode(request.newPassword()));
        user.setRefreshTokenHash(null);
        userRepository.save(user);

        return new ApiMessageResponse("Password has been reset successfully");
    }

    private AuthResponse issueTokens(User user) {
        String accessToken = jwtService.generateAccessToken(user.getEmail());
        String refreshToken = jwtService.generateRefreshToken(user.getEmail());
        user.setRefreshTokenHash(hashToken(refreshToken));
        userRepository.save(user);
        return new AuthResponse(accessToken, refreshToken, UserResponse.from(user));
    }

    private void purgeExpiredResetTokens() {
        Instant now = Instant.now();
        resetTokenIndex.entrySet().removeIf(entry -> entry.getValue().expiresAt().isBefore(now));
    }

    private String generateResetToken() {
        byte[] bytes = new byte[24];
        new SecureRandom().nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }

    private String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException exception) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Token hash algorithm unavailable");
        }
    }

    private record ResetTokenRecord(String email, Instant expiresAt) {
    }
}
