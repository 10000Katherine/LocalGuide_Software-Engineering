package com.localguide.modules.authuser.dto;

import com.localguide.modules.authuser.domain.User;
import com.localguide.modules.authuser.domain.UserRole;

public record UserResponse(
        Long id,
        String email,
        String firstName,
        String lastName,
        String phone,
        UserRole role
) {
    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getPhone(),
                user.getRole()
        );
    }
}
