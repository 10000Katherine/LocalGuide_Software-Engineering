package com.localguide.modules.guidetour.repository;

import com.localguide.modules.guidetour.domain.AvailabilitySlot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface AvailabilitySlotRepository extends JpaRepository<AvailabilitySlot, Long> {
    List<AvailabilitySlot> findByGuideIdOrderBySlotDateAscStartTimeAsc(Long guideId);

    List<AvailabilitySlot> findByGuideIdAndSlotDateOrderByStartTimeAsc(Long guideId, LocalDate slotDate);

    boolean existsByGuideIdAndSlotDateAndStartTimeLessThanEqualAndEndTimeGreaterThanEqual(
            Long guideId,
            LocalDate slotDate,
            LocalTime startTime,
            LocalTime endTime
    );

    void deleteByGuideId(Long guideId);
}
