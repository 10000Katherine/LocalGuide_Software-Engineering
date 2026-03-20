package com.localguide.modules.guidetour.repository;

import com.localguide.modules.guidetour.domain.Guide;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface GuideRepository extends JpaRepository<Guide, Long>, JpaSpecificationExecutor<Guide> {
    Optional<Guide> findByUserId(Long userId);

    Optional<Guide> findByUserEmail(String email);
}
