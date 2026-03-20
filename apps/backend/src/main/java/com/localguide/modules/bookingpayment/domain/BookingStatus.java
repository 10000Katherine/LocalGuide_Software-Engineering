package com.localguide.modules.bookingpayment.domain;

public enum BookingStatus {
    CREATED,
    PENDING_PAYMENT,
    PAYMENT_FAILED,
    CONFIRMED,
    IN_PROGRESS,
    COMPLETED,
    CANCELLED_BY_TOURIST,
    CANCELLED_BY_GUIDE,
    DISPUTED
}
