package com.localguide.modules.reviewadmin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SubmitVerificationRequest(
        @NotBlank @Size(max = 500) String documentUrl,
        @Size(max = 1000) String submissionNote
) {
}
