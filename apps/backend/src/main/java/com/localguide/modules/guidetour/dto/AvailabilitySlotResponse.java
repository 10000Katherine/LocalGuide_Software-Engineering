package com.localguide.modules.guidetour.dto;

import com.localguide.modules.guidetour.domain.AvailabilitySlot;

import java.time.LocalDate;
import java.time.LocalTime;

public record AvailabilitySlotResponse(
        Long id,
        Long guideId,
        LocalDate slotDate,
        LocalTime startTime,
        LocalTime endTime
) {
    public static AvailabilitySlotResponse from(AvailabilitySlot slot) {
        return new AvailabilitySlotResponse(
                slot.getId(),
                slot.getGuide().getId(),
                slot.getSlotDate(),
                slot.getStartTime(),
                slot.getEndTime()
        );
    }
}
