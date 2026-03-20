package com.localguide.modules.guidetour.dto;

import java.math.BigDecimal;
import java.util.List;

public record GuideProfileResponse(
        Long id,
        String firstName,
        String lastName,
        String province,
        String city,
        String neighborhood,
        String language,
        String bio,
        String photoUrl,
        String verificationStatus,
        BigDecimal avgRating,
        Integer totalReviews,
        List<TourResponse> tours
) {
    public static GuideProfileResponse of(GuideSummaryResponse summary, List<TourResponse> tours) {
        return new GuideProfileResponse(
                summary.id(),
                summary.firstName(),
                summary.lastName(),
                summary.province(),
                summary.city(),
                summary.neighborhood(),
                summary.language(),
                summary.bio(),
                summary.photoUrl(),
                summary.verificationStatus(),
                summary.avgRating(),
                summary.totalReviews(),
                tours
        );
    }
}
