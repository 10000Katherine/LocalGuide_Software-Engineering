package com.localguide.modules.reviewadmin.dto;

import com.localguide.modules.reviewadmin.domain.Review;

import java.time.Instant;

public record ReviewResponse(
        Long id,
        String bookingReference,
        Long touristId,
        String touristName,
        Long guideId,
        String guideName,
        Long tourId,
        int rating,
        String comment,
        String guideReply,
        Instant createdAt,
        Instant updatedAt
) {
    public static ReviewResponse from(Review review) {
        return new ReviewResponse(
                review.getId(),
                review.getBookingReference(),
                review.getTourist().getId(),
                review.getTourist().getFirstName() + " " + review.getTourist().getLastName(),
                review.getGuide().getId(),
                review.getGuide().getFirstName() + " " + review.getGuide().getLastName(),
                review.getTourId(),
                review.getRating(),
                review.getComment(),
                review.getGuideReply(),
                review.getCreatedAt(),
                review.getUpdatedAt()
        );
    }
}
