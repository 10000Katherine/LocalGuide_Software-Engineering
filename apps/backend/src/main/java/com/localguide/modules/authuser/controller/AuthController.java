package com.localguide.modules.authuser.controller;

import com.localguide.modules.authuser.dto.ApiMessageResponse;
import com.localguide.modules.authuser.dto.AuthResponse;
import com.localguide.modules.authuser.dto.ConfirmResetPasswordRequest;
import com.localguide.modules.authuser.dto.LoginRequest;
import com.localguide.modules.authuser.dto.RefreshTokenRequest;
import com.localguide.modules.authuser.dto.RegisterRequest;
import com.localguide.modules.authuser.dto.ResetPasswordRequest;
import com.localguide.modules.authuser.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse register(@RequestBody @Valid RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody @Valid LoginRequest request) {
        return authService.login(request);
    }

    @PostMapping("/refresh")
    public AuthResponse refresh(@RequestBody @Valid RefreshTokenRequest request) {
        return authService.refresh(request);
    }

    @PostMapping("/reset-password/request")
    public ApiMessageResponse requestResetPassword(@RequestBody @Valid ResetPasswordRequest request) {
        return authService.requestResetPassword(request);
    }

    @PostMapping("/reset-password/confirm")
    public ApiMessageResponse confirmResetPassword(@RequestBody @Valid ConfirmResetPasswordRequest request) {
        return authService.confirmResetPassword(request);
    }
}
