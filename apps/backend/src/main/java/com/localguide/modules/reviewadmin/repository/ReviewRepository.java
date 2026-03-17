package com.localguide.modules.reviewadmin.repository;

import com.localguide.modules.reviewadmin.domain.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByGuideIdOrderByCreatedAtDesc(Long guideId);
    List<Review> findByTourIdOrderByCreatedAtDesc(Long tourId);
    Optional<Review> findByBookingReference(String bookingReference);
    long countByGuideId(Long guideId);
}
