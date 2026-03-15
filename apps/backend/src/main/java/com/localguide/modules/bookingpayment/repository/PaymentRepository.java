package com.localguide.modules.bookingpayment.repository;

import com.localguide.modules.bookingpayment.domain.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByBookingId(Long bookingId);

    Optional<Payment> findByProviderReference(String providerReference);
}
