package com.localguide.modules.reviewadmin.repository;

import com.localguide.modules.reviewadmin.domain.GuideVerificationRequest;
import com.localguide.modules.reviewadmin.domain.GuideVerificationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GuideVerificationRequestRepository extends JpaRepository<GuideVerificationRequest, Long> {
    Optional<GuideVerificationRequest> findByGuideId(Long guideId);
    List<GuideVerificationRequest> findByStatusOrderByCreatedAtAsc(GuideVerificationStatus status);
}
