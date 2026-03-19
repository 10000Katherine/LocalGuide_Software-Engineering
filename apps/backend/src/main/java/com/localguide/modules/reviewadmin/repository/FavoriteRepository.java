package com.localguide.modules.reviewadmin.repository;

import com.localguide.modules.reviewadmin.domain.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    List<Favorite> findByTouristIdOrderByCreatedAtDesc(Long touristId);
    Optional<Favorite> findByTouristIdAndGuideId(Long touristId, Long guideId);
    long countByGuideId(Long guideId);
}
