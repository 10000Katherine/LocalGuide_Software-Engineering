package com.localguide.modules.guidetour.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record UpdateTourRequest(
        @NotBlank @Size(max = 120) String title,
        @NotBlank @Size(max = 4000) String description,
        @NotBlank @Size(max = 60) String category,
        @NotBlank @Size(max = 100) String province,
        @NotBlank @Size(max = 100) String city,
        @NotBlank @Size(max = 120) String language,
        @NotNull @DecimalMin("0.0") BigDecimal price,
        @NotNull @DecimalMin("0.1") BigDecimal durationHours,
        @NotNull @Min(1) Integer groupSize
) {
}
