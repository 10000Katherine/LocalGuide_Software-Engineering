package com.localguide.modules.reviewadmin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record VerificationDecisionRequest(
        @NotBlank String decision,
        @Size(max = 1000) String reviewNote
) {
}
