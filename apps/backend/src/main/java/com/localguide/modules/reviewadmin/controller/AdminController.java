package com.localguide.modules.reviewadmin.controller;

import com.localguide.modules.reviewadmin.dto.*;
import com.localguide.modules.reviewadmin.service.AdminService;
import com.localguide.modules.reviewadmin.service.ReviewService;
import com.localguide.modules.reviewadmin.service.VerificationService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {
    private final AdminService adminService;
    private final VerificationService verificationService;
    private final ReviewService reviewService;

    public AdminController(AdminService adminService, VerificationService verificationService, ReviewService reviewService) {
        this.adminService = adminService;
        this.verificationService = verificationService;
        this.reviewService = reviewService;
    }

    @GetMapping("/verifications")
    public List<GuideVerificationResponse> pendingVerifications() {
        return verificationService.listPending();
    }

    @PutMapping("/verifications/{requestId}")
    public GuideVerificationResponse reviewVerification(Authentication authentication,
                                                        @PathVariable Long requestId,
                                                        @RequestBody @Valid VerificationDecisionRequest request) {
        return verificationService.review(authentication.getName(), requestId, request);
    }

    @GetMapping("/users")
    public List<AdminUserResponse> listUsers(Authentication authentication) {
        return adminService.listUsers(authentication.getName());
    }

    @PutMapping("/users/{userId}")
    public AdminUserResponse updateUserStatus(Authentication authentication,
                                              @PathVariable Long userId,
                                              @RequestBody @Valid UpdateUserStatusRequest request) {
        return adminService.updateUserStatus(authentication.getName(), userId, request);
    }

    @GetMapping("/analytics")
    public AdminAnalyticsResponse analytics(Authentication authentication) {
        return adminService.analytics(authentication.getName());
    }

    @GetMapping("/guides/{guideId}/earnings")
    public GuideEarningsResponse guideEarnings(@PathVariable Long guideId) {
        ReviewService.GuideEarningsProjection projection = reviewService.getGuideEarnings(guideId);
        return new GuideEarningsResponse(
                projection.guideId(),
                projection.guideName(),
                projection.totalReviews(),
                projection.totalFavorites(),
                projection.averageRating(),
                "Payment integration is pending from Module C, so this view currently shows engagement-based KPIs."
        );
    }
}
