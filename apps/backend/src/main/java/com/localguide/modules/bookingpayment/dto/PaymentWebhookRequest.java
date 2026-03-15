package com.localguide.modules.bookingpayment.dto;

import jakarta.validation.constraints.NotBlank;

public record PaymentWebhookRequest(
        @NotBlank String paymentIntentId,
        @NotBlank String status
) {
}
