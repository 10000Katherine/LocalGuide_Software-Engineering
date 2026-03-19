package com.localguide.modules.reviewadmin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ReplyReviewRequest(
        @NotBlank @Size(max = 1500) String reply
) {
}
