package com.localguide.modules.bookingpayment.dto;

public record PaymentIntentResponse(
        Long bookingId,
        String paymentIntentId,
        String clientSecret
) {
}
