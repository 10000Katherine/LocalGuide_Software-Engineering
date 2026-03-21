package com.localguide.modules.reviewadmin.controller;

import com.localguide.modules.reviewadmin.dto.GuideVerificationResponse;
import com.localguide.modules.reviewadmin.dto.SubmitVerificationRequest;
import com.localguide.modules.reviewadmin.service.VerificationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/guide-verifications/me")
public class VerificationController {
    private final VerificationService verificationService;

    public VerificationController(VerificationService verificationService) {
        this.verificationService = verificationService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public GuideVerificationResponse submit(Authentication authentication,
                                            @RequestBody @Valid SubmitVerificationRequest request) {
        return verificationService.submit(authentication.getName(), request);
    }

    @GetMapping
    public GuideVerificationResponse getMine(Authentication authentication) {
        return verificationService.getMine(authentication.getName());
    }
}
