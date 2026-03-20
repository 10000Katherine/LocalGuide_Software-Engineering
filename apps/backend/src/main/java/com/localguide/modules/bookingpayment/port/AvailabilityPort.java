package com.localguide.modules.bookingpayment.port;

import java.time.LocalDate;
import java.time.LocalTime;

public interface AvailabilityPort {
    boolean isAvailable(Long tourId, LocalDate bookingDate, LocalTime startTime);
}
