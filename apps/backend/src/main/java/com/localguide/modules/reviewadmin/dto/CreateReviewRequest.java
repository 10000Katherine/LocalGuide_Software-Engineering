package com.localguide.modules.reviewadmin.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateReviewRequest(
        @NotBlank String bookingReference,
        @NotNull Long guideId,
        Long tourId,
        @Min(1) @Max(5) int rating,
        @NotBlank @Size(max = 1500) String comment
) {
}
