package com.localguide.modules.bookingpayment.stub;

import com.localguide.modules.bookingpayment.port.PaymentGatewayPort;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class StubPaymentGatewayPort implements PaymentGatewayPort {
    private final Map<String, BigDecimal> intents = new ConcurrentHashMap<>();
    private final Map<String, BigDecimal> refunds = new ConcurrentHashMap<>();

    @Override
    public PaymentIntentResult createPaymentIntent(Long bookingId, BigDecimal amount) {
        String paymentIntentId = "pi_stub_" + bookingId + "_" + UUID.randomUUID();
        String clientSecret = "cs_stub_" + UUID.randomUUID();
        intents.put(paymentIntentId, amount);
        return new PaymentIntentResult(paymentIntentId, clientSecret);
    }

    @Override
    public void refundPaymentIntent(String paymentIntentId, BigDecimal amount) {
        refunds.put(paymentIntentId, amount);
    }

    public Map<String, BigDecimal> getIntents() {
        return intents;
    }

    public Map<String, BigDecimal> getRefunds() {
        return refunds;
    }
}
