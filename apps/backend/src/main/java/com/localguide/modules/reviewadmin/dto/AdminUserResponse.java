package com.localguide.modules.reviewadmin.dto;

import com.localguide.modules.authuser.domain.User;
import com.localguide.modules.reviewadmin.domain.GuideVerificationStatus;

public record AdminUserResponse(
        Long id,
        String email,
        String firstName,
        String lastName,
        String phone,
        String role,
        boolean active,
        GuideVerificationStatus verificationStatus
) {
    public static AdminUserResponse from(User user, GuideVerificationStatus verificationStatus) {
        return new AdminUserResponse(
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getPhone(),
                user.getRole().name(),
                user.isActive(),
                verificationStatus
        );
    }
}
