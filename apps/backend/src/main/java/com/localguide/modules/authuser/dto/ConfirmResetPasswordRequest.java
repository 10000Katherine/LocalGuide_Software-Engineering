package com.localguide.modules.authuser.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ConfirmResetPasswordRequest(
        @NotBlank String resetToken,
        @NotBlank @Size(min = 8, max = 72) String newPassword
) {
}
