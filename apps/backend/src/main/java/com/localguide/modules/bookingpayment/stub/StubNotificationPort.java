package com.localguide.modules.bookingpayment.stub;

import com.localguide.modules.bookingpayment.port.NotificationPort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class StubNotificationPort implements NotificationPort {
    private final List<String> sentMessages = new CopyOnWriteArrayList<>();

    @Override
    public void sendBookingConfirmed(String email, Long bookingId) {
        sentMessages.add("CONFIRMED|" + email + "|" + bookingId);
    }

    @Override
    public void sendBookingCancelled(String email, Long bookingId) {
        sentMessages.add("CANCELLED|" + email + "|" + bookingId);
    }

    public List<String> getSentMessages() {
        return sentMessages;
    }
}
