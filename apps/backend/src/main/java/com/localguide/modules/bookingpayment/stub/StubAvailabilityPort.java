package com.localguide.modules.bookingpayment.stub;

import com.localguide.modules.bookingpayment.port.AvailabilityPort;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class StubAvailabilityPort implements AvailabilityPort {
    private final Set<String> blockedSlots = ConcurrentHashMap.newKeySet();

    @Override
    public boolean isAvailable(Long tourId, LocalDate bookingDate, LocalTime startTime) {
        return !blockedSlots.contains(slotKey(tourId, bookingDate, startTime));
    }

    public void block(Long tourId, LocalDate bookingDate, LocalTime startTime) {
        blockedSlots.add(slotKey(tourId, bookingDate, startTime));
    }

    public void clear() {
        blockedSlots.clear();
    }

    private String slotKey(Long tourId, LocalDate bookingDate, LocalTime startTime) {
        return tourId + "|" + bookingDate + "|" + startTime;
    }
}
