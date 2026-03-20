package com.localguide.modules.guidetour.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

public record AvailabilitySlotRequest(
        @NotNull LocalDate slotDate,
        @NotNull LocalTime startTime,
        @NotNull LocalTime endTime
) {
}
