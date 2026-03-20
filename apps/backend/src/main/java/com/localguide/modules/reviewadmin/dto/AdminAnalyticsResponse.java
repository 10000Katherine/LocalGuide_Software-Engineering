package com.localguide.modules.reviewadmin.dto;

public record AdminAnalyticsResponse(
        long totalUsers,
        long activeUsers,
        long totalGuides,
        long totalTourists,
        long verifiedGuides,
        long pendingVerifications,
        long totalReviews,
        long totalFavorites
) {
}
