package com.localguide.modules.bookingpayment.service;

import com.localguide.common.exception.ApiException;
import com.localguide.modules.authuser.domain.User;
import com.localguide.modules.authuser.domain.UserRole;
import com.localguide.modules.authuser.repository.UserRepository;
import com.localguide.modules.bookingpayment.domain.Booking;
import com.localguide.modules.bookingpayment.domain.BookingStatus;
import com.localguide.modules.bookingpayment.domain.Payment;
import com.localguide.modules.bookingpayment.domain.PaymentStatus;
import com.localguide.modules.bookingpayment.dto.CreateBookingRequest;
import com.localguide.modules.bookingpayment.dto.CreatePaymentIntentRequest;
import com.localguide.modules.bookingpayment.dto.PaymentWebhookRequest;
import com.localguide.modules.bookingpayment.port.AvailabilityPort;
import com.localguide.modules.bookingpayment.port.NotificationPort;
import com.localguide.modules.bookingpayment.port.PaymentGatewayPort;
import com.localguide.modules.bookingpayment.repository.BookingRepository;
import com.localguide.modules.bookingpayment.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private AvailabilityPort availabilityPort;
    @Mock
    private PaymentGatewayPort paymentGatewayPort;
    @Mock
    private NotificationPort notificationPort;

    private BookingService bookingService;

    @BeforeEach
    void setUp() {
        bookingService = new BookingService(
                bookingRepository,
                paymentRepository,
                userRepository,
                availabilityPort,
                paymentGatewayPort,
                notificationPort
        );
    }

    @Test
    void createBookingRejectsUnavailableSlot() {
        User tourist = tourist("tourist@example.com");
        CreateBookingRequest request = new CreateBookingRequest(
                101L,
                LocalDate.now().plusDays(1),
                LocalTime.of(10, 0),
                2,
                new BigDecimal("120.00")
        );

        when(userRepository.findByEmail("tourist@example.com")).thenReturn(Optional.of(tourist));
        when(availabilityPort.isAvailable(request.tourId(), request.bookingDate(), request.startTime())).thenReturn(false);

        ApiException exception = assertThrows(
                ApiException.class,
                () -> bookingService.createBooking("tourist@example.com", request)
        );
        assertEquals(HttpStatus.CONFLICT, exception.getStatus());
    }

    @Test
    void createBookingRejectsDoubleBookedSlot() {
        User tourist = tourist("tourist@example.com");
        CreateBookingRequest request = new CreateBookingRequest(
                101L,
                LocalDate.now().plusDays(1),
                LocalTime.of(10, 0),
                2,
                new BigDecimal("120.00")
        );

        when(userRepository.findByEmail("tourist@example.com")).thenReturn(Optional.of(tourist));
        when(availabilityPort.isAvailable(request.tourId(), request.bookingDate(), request.startTime())).thenReturn(true);
        when(bookingRepository.existsByTourIdAndBookingDateAndStartTimeAndStatusIn(
                eq(request.tourId()),
                eq(request.bookingDate()),
                eq(request.startTime()),
                any()
        )).thenReturn(true);

        ApiException exception = assertThrows(
                ApiException.class,
                () -> bookingService.createBooking("tourist@example.com", request)
        );
        assertEquals(HttpStatus.CONFLICT, exception.getStatus());
    }

    @Test
    void createPaymentIntentUpdatesBookingAndPayment() {
        Booking booking = booking(10L, "tourist@example.com", BookingStatus.CREATED, new BigDecimal("100.00"), "");
        when(bookingRepository.findByIdAndTouristEmail(10L, "tourist@example.com")).thenReturn(Optional.of(booking));
        when(paymentGatewayPort.createPaymentIntent(10L, new BigDecimal("100.00")))
                .thenReturn(new PaymentGatewayPort.PaymentIntentResult("pi_test_10", "cs_test_10"));
        when(paymentRepository.findByBookingId(10L)).thenReturn(Optional.empty());
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var response = bookingService.createPaymentIntent(
                "tourist@example.com",
                new CreatePaymentIntentRequest(10L)
        );

        assertEquals(10L, response.bookingId());
        assertEquals("pi_test_10", response.paymentIntentId());
        assertEquals("cs_test_10", response.clientSecret());

        ArgumentCaptor<Booking> bookingCaptor = ArgumentCaptor.forClass(Booking.class);
        verify(bookingRepository).save(bookingCaptor.capture());
        assertEquals(BookingStatus.PENDING_PAYMENT, bookingCaptor.getValue().getStatus());
        assertEquals("pi_test_10", bookingCaptor.getValue().getStripePaymentIntentId());

        ArgumentCaptor<Payment> paymentCaptor = ArgumentCaptor.forClass(Payment.class);
        verify(paymentRepository).save(paymentCaptor.capture());
        assertEquals(PaymentStatus.PENDING, paymentCaptor.getValue().getStatus());
    }

    @Test
    void handlePaymentWebhookSuccessCalculatesCommission() {
        Booking booking = booking(11L, "tourist@example.com", BookingStatus.PENDING_PAYMENT, new BigDecimal("100.00"), "pi_test_11");
        Payment payment = new Payment();
        payment.setBooking(booking);

        when(bookingRepository.findByStripePaymentIntentId("pi_test_11")).thenReturn(Optional.of(booking));
        when(paymentRepository.findByBookingId(11L)).thenReturn(Optional.of(payment));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        bookingService.handlePaymentWebhook(new PaymentWebhookRequest("pi_test_11", "SUCCEEDED"));

        ArgumentCaptor<Payment> paymentCaptor = ArgumentCaptor.forClass(Payment.class);
        verify(paymentRepository).save(paymentCaptor.capture());
        assertEquals(PaymentStatus.SUCCEEDED, paymentCaptor.getValue().getStatus());
        assertEquals(new BigDecimal("12.00"), paymentCaptor.getValue().getCommission());
        assertEquals(new BigDecimal("88.00"), paymentCaptor.getValue().getGuidePayout());

        ArgumentCaptor<Booking> bookingCaptor = ArgumentCaptor.forClass(Booking.class);
        verify(bookingRepository).save(bookingCaptor.capture());
        assertEquals(BookingStatus.CONFIRMED, bookingCaptor.getValue().getStatus());
        verify(notificationPort).sendBookingConfirmed("tourist@example.com", 11L);
    }

    @Test
    void cancelBookingRefundsPaidBooking() {
        Booking booking = booking(12L, "tourist@example.com", BookingStatus.CONFIRMED, new BigDecimal("140.00"), "pi_test_12");
        Payment payment = new Payment();
        payment.setBooking(booking);
        payment.setStatus(PaymentStatus.SUCCEEDED);

        when(bookingRepository.findByIdAndTouristEmail(12L, "tourist@example.com")).thenReturn(Optional.of(booking));
        when(paymentRepository.findByBookingId(12L)).thenReturn(Optional.of(payment));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var response = bookingService.cancelBooking("tourist@example.com", 12L);
        assertEquals("Booking cancelled successfully", response.message());

        verify(paymentGatewayPort).refundPaymentIntent("pi_test_12", new BigDecimal("140.00"));
        ArgumentCaptor<Payment> paymentCaptor = ArgumentCaptor.forClass(Payment.class);
        verify(paymentRepository).save(paymentCaptor.capture());
        assertEquals(PaymentStatus.REFUNDED, paymentCaptor.getValue().getStatus());

        ArgumentCaptor<Booking> bookingCaptor = ArgumentCaptor.forClass(Booking.class);
        verify(bookingRepository).save(bookingCaptor.capture());
        assertEquals(BookingStatus.CANCELLED_BY_TOURIST, bookingCaptor.getValue().getStatus());
        verify(notificationPort).sendBookingCancelled("tourist@example.com", 12L);
    }

    private User tourist(String email) {
        User user = new User();
        user.setEmail(email);
        user.setRole(UserRole.TOURIST);
        user.setActive(true);
        return user;
    }

    private Booking booking(Long id,
                            String email,
                            BookingStatus status,
                            BigDecimal totalPrice,
                            String paymentIntentId) {
        Booking booking = new Booking();
        setField(booking, "id", id);
        booking.setTourist(tourist(email));
        booking.setTourId(500L);
        booking.setBookingDate(LocalDate.now().plusDays(1));
        booking.setStartTime(LocalTime.of(14, 0));
        booking.setNumPeople(2);
        booking.setTotalPrice(totalPrice);
        booking.setStatus(status);
        booking.setStripePaymentIntentId(paymentIntentId);
        return booking;
    }

    private void setField(Object target, String fieldName, Object value) {
        try {
            Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Unable to set test field", exception);
        }
    }
}
