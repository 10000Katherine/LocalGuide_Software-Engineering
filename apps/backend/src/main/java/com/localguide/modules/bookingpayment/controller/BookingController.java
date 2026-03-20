package com.localguide.modules.bookingpayment.controller;

import com.localguide.modules.bookingpayment.dto.ApiMessageResponse;
import com.localguide.modules.bookingpayment.dto.BookingResponse;
import com.localguide.modules.bookingpayment.dto.CreateBookingRequest;
import com.localguide.modules.bookingpayment.service.BookingService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/bookings")
public class BookingController {
    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingResponse createBooking(Authentication authentication, @RequestBody @Valid CreateBookingRequest request) {
        return bookingService.createBooking(authentication.getName(), request);
    }

    @GetMapping
    public List<BookingResponse> listMyBookings(Authentication authentication) {
        return bookingService.listMyBookings(authentication.getName());
    }

    @GetMapping("/{id}")
    public BookingResponse getMyBooking(Authentication authentication, @PathVariable Long id) {
        return bookingService.getMyBooking(authentication.getName(), id);
    }

    @PutMapping("/{id}/cancel")
    public ApiMessageResponse cancelBooking(Authentication authentication, @PathVariable Long id) {
        return bookingService.cancelBooking(authentication.getName(), id);
    }
}
