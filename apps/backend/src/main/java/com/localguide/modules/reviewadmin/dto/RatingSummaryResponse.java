package com.localguide.modules.reviewadmin.dto;

public record RatingSummaryResponse(
        Long guideId,
        double averageRating,
        long totalReviews,
        long favoriteCount
) {
}
