package com.localguide.modules.guidetour.dto;

import com.localguide.modules.authuser.domain.User;
import com.localguide.modules.guidetour.domain.Guide;

import java.math.BigDecimal;

public record GuideSummaryResponse(
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
        Integer totalReviews
) {
    public static GuideSummaryResponse from(Guide guide) {
        User user = guide.getUser();
        String firstName = user != null && user.getFirstName() != null ? user.getFirstName() : "Guide";
        String lastName = user != null && user.getLastName() != null ? user.getLastName() : "";

        return new GuideSummaryResponse(
                guide.getId(),
                firstName,
                lastName,
                guide.getProvince(),
                guide.getCity(),
                guide.getNeighborhood(),
                guide.getLanguages(),
                guide.getBio(),
                guide.getPhotoUrl(),
                guide.getVerificationStatus() == null ? null : guide.getVerificationStatus().name(),
                guide.getAvgRating(),
                guide.getTotalReviews()
        );
    }
}
