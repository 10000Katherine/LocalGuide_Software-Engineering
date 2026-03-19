package com.localguide.modules.bookingpayment.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CreatePaymentIntentRequest(
        @NotNull @Positive Long bookingId
) {
}
