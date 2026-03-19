package com.localguide.modules.bookingpayment.dto;

import com.localguide.modules.bookingpayment.domain.Booking;
import com.localguide.modules.bookingpayment.domain.BookingStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

public record BookingResponse(
        Long id,
        Long tourId,
        LocalDate bookingDate,
        LocalTime startTime,
        int numPeople,
        BigDecimal totalPrice,
        BookingStatus status,
        String stripePaymentIntentId
) {
    public static BookingResponse from(Booking booking) {
        return new BookingResponse(
                booking.getId(),
                booking.getTourId(),
                booking.getBookingDate(),
                booking.getStartTime(),
                booking.getNumPeople(),
                booking.getTotalPrice(),
                booking.getStatus(),
                booking.getStripePaymentIntentId()
        );
    }
}
