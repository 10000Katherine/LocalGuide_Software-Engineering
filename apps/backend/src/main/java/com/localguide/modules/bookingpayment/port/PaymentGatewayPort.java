package com.localguide.modules.bookingpayment.port;

import java.math.BigDecimal;

public interface PaymentGatewayPort {
    PaymentIntentResult createPaymentIntent(Long bookingId, BigDecimal amount);

    void refundPaymentIntent(String paymentIntentId, BigDecimal amount);

    record PaymentIntentResult(String paymentIntentId, String clientSecret) {
    }
}
