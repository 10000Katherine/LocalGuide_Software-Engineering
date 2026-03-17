package com.localguide.modules.reviewadmin.dto;

public record GuideEarningsResponse(
        Long guideId,
        String guideName,
        long totalReviews,
        long totalFavorites,
        double averageRating,
        String note
) {
}
