package com.localguide.modules.reviewadmin.dto;

import com.localguide.modules.reviewadmin.domain.GuideVerificationRequest;
import com.localguide.modules.reviewadmin.domain.GuideVerificationStatus;

import java.time.Instant;

public record GuideVerificationResponse(
        Long id,
        Long guideId,
        String guideName,
        String guideEmail,
        GuideVerificationStatus status,
        String documentUrl,
        String submissionNote,
        String reviewNote,
        String reviewedByName,
        Instant reviewedAt,
        Instant createdAt,
        Instant updatedAt
) {
    public static GuideVerificationResponse from(GuideVerificationRequest request) {
        return new GuideVerificationResponse(
                request.getId(),
                request.getGuide().getId(),
                request.getGuide().getFirstName() + " " + request.getGuide().getLastName(),
                request.getGuide().getEmail(),
                request.getStatus(),
                request.getDocumentUrl(),
                request.getSubmissionNote(),
                request.getReviewNote(),
                request.getReviewedBy() == null ? null : request.getReviewedBy().getFirstName() + " " + request.getReviewedBy().getLastName(),
                request.getReviewedAt(),
                request.getCreatedAt(),
                request.getUpdatedAt()
        );
    }
}
