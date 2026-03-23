package com.localguide.modules.bookingpayment.repository;

import com.localguide.modules.bookingpayment.domain.Booking;
import com.localguide.modules.bookingpayment.domain.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByTouristEmailOrderByBookingDateDescStartTimeDesc(String touristEmail);

    Optional<Booking> findByIdAndTouristEmail(Long id, String touristEmail);

    Optional<Booking> findByStripePaymentIntentId(String stripePaymentIntentId);

    List<Booking> findByTourIdInOrderByBookingDateDescStartTimeDesc(Collection<Long> tourIds);

    Optional<Booking> findByIdAndTourIdIn(Long id, Collection<Long> tourIds);

    boolean existsByTourIdAndBookingDateAndStartTimeAndStatusIn(
            Long tourId,
            LocalDate bookingDate,
            LocalTime startTime,
            Collection<BookingStatus> statuses
    );
}
