package com.localguide.modules.reviewadmin.service;

import com.localguide.common.exception.ApiException;
import com.localguide.modules.authuser.domain.User;
import com.localguide.modules.authuser.domain.UserRole;
import com.localguide.modules.authuser.repository.UserRepository;
import com.localguide.modules.reviewadmin.domain.GuideVerificationRequest;
import com.localguide.modules.reviewadmin.domain.GuideVerificationStatus;
import com.localguide.modules.reviewadmin.dto.GuideVerificationResponse;
import com.localguide.modules.reviewadmin.dto.SubmitVerificationRequest;
import com.localguide.modules.reviewadmin.dto.VerificationDecisionRequest;
import com.localguide.modules.reviewadmin.repository.GuideVerificationRequestRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class VerificationService {
    private final GuideVerificationRequestRepository verificationRepository;
    private final UserRepository userRepository;

    public VerificationService(GuideVerificationRequestRepository verificationRepository, UserRepository userRepository) {
        this.verificationRepository = verificationRepository;
        this.userRepository = userRepository;
    }

    public GuideVerificationResponse submit(String guideEmail, SubmitVerificationRequest request) {
        User guide = getRequiredUserByEmail(guideEmail);
        requireRole(guide, UserRole.GUIDE, "Only guides can submit verification requests");

        GuideVerificationRequest entity = verificationRepository.findByGuideId(guide.getId())
                .orElseGet(GuideVerificationRequest::new);
        entity.setGuide(guide);
        entity.setDocumentUrl(request.documentUrl().trim());
        entity.setSubmissionNote(request.submissionNote());
        entity.setReviewNote(null);
        entity.setReviewedAt(null);
        entity.setReviewedBy(null);
        entity.setStatus(GuideVerificationStatus.PENDING);
        return GuideVerificationResponse.from(verificationRepository.save(entity));
    }

    public List<GuideVerificationResponse> listPending() {
        return verificationRepository.findByStatusOrderByCreatedAtAsc(GuideVerificationStatus.PENDING)
                .stream()
                .map(GuideVerificationResponse::from)
                .toList();
    }

    public GuideVerificationResponse review(String adminEmail, Long requestId, VerificationDecisionRequest request) {
        User admin = getRequiredUserByEmail(adminEmail);
        requireRole(admin, UserRole.ADMIN, "Only admins can review guide verification requests");

        GuideVerificationRequest entity = verificationRepository.findById(requestId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Verification request not found"));

        GuideVerificationStatus decision = parseDecision(request.decision());
        entity.setStatus(decision);
        entity.setReviewNote(request.reviewNote());
        entity.setReviewedBy(admin);
        entity.setReviewedAt(Instant.now());
        return GuideVerificationResponse.from(verificationRepository.save(entity));
    }

    public GuideVerificationResponse getMine(String guideEmail) {
        User guide = getRequiredUserByEmail(guideEmail);
        requireRole(guide, UserRole.GUIDE, "Only guides can view verification status");
        GuideVerificationRequest entity = verificationRepository.findByGuideId(guide.getId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Verification request not found"));
        return GuideVerificationResponse.from(entity);
    }

    private GuideVerificationStatus parseDecision(String rawDecision) {
        String normalized = rawDecision.trim().toUpperCase();
        return switch (normalized) {
            case "APPROVE", "APPROVED" -> GuideVerificationStatus.APPROVED;
            case "REJECT", "REJECTED" -> GuideVerificationStatus.REJECTED;
            default -> throw new ApiException(HttpStatus.BAD_REQUEST, "Decision must be APPROVE or REJECT");
        };
    }

    private User getRequiredUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found"));
    }

    private void requireRole(User user, UserRole expected, String message) {
        if (user.getRole() != expected) {
            throw new ApiException(HttpStatus.FORBIDDEN, message);
        }
    }
}
