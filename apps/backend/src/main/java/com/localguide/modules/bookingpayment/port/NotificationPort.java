package com.localguide.modules.bookingpayment.port;

public interface NotificationPort {
    void sendBookingConfirmed(String email, Long bookingId);

    void sendBookingCancelled(String email, Long bookingId);
}
