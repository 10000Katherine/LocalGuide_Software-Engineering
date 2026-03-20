package com.localguide.modules.bookingpayment.controller;

import com.localguide.modules.bookingpayment.dto.ApiMessageResponse;
import com.localguide.modules.bookingpayment.dto.CreatePaymentIntentRequest;
import com.localguide.modules.bookingpayment.dto.PaymentIntentResponse;
import com.localguide.modules.bookingpayment.dto.PaymentWebhookRequest;
import com.localguide.modules.bookingpayment.service.BookingService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {
    private final BookingService bookingService;

    public PaymentController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping("/create-intent")
    public PaymentIntentResponse createIntent(Authentication authentication,
                                              @RequestBody @Valid CreatePaymentIntentRequest request) {
        return bookingService.createPaymentIntent(authentication.getName(), request);
    }

    @PostMapping("/webhook")
    public ApiMessageResponse webhook(@RequestBody @Valid PaymentWebhookRequest request) {
        return bookingService.handlePaymentWebhook(request);
    }
}
