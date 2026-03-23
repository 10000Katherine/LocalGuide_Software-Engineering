package com.localguide.modules.bookingpayment.service;

import com.localguide.common.exception.ApiException;
import com.localguide.modules.authuser.domain.User;
import com.localguide.modules.authuser.domain.UserRole;
import com.localguide.modules.authuser.repository.UserRepository;
import com.localguide.modules.bookingpayment.domain.Booking;
import com.localguide.modules.bookingpayment.domain.BookingStatus;
import com.localguide.modules.bookingpayment.domain.Payment;
import com.localguide.modules.bookingpayment.domain.PaymentStatus;
import com.localguide.modules.bookingpayment.dto.ApiMessageResponse;
import com.localguide.modules.bookingpayment.dto.BookingResponse;
import com.localguide.modules.bookingpayment.dto.CreateBookingRequest;
import com.localguide.modules.bookingpayment.dto.CreatePaymentIntentRequest;
import com.localguide.modules.bookingpayment.dto.PaymentIntentResponse;
import com.localguide.modules.bookingpayment.dto.PaymentWebhookRequest;
import com.localguide.modules.guidetour.domain.Guide;
import com.localguide.modules.guidetour.domain.Tour;
import com.localguide.modules.guidetour.repository.GuideRepository;
import com.localguide.modules.guidetour.repository.TourRepository;
import com.localguide.modules.bookingpayment.port.AvailabilityPort;
import com.localguide.modules.bookingpayment.port.NotificationPort;
import com.localguide.modules.bookingpayment.port.PaymentGatewayPort;
import com.localguide.modules.bookingpayment.repository.BookingRepository;
import com.localguide.modules.bookingpayment.repository.PaymentRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

@Service
public class BookingService {
    private static final BigDecimal COMMISSION_RATE = new BigDecimal("0.12");
    private static final Set<BookingStatus> ACTIVE_SLOT_STATUSES = Set.of(
            BookingStatus.CREATED,
            BookingStatus.PENDING_PAYMENT,
            BookingStatus.CONFIRMED,
            BookingStatus.IN_PROGRESS,
            BookingStatus.COMPLETED
    );

    private final BookingRepository bookingRepository;
    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final GuideRepository guideRepository;
    private final TourRepository tourRepository;
    private final AvailabilityPort availabilityPort;
    private final PaymentGatewayPort paymentGatewayPort;
    private final NotificationPort notificationPort;

    public BookingService(
            BookingRepository bookingRepository,
            PaymentRepository paymentRepository,
            UserRepository userRepository,
            GuideRepository guideRepository,
            TourRepository tourRepository,
            AvailabilityPort availabilityPort,
            PaymentGatewayPort paymentGatewayPort,
            NotificationPort notificationPort
    ) {
        this.bookingRepository = bookingRepository;
        this.paymentRepository = paymentRepository;
        this.userRepository = userRepository;
        this.guideRepository = guideRepository;
        this.tourRepository = tourRepository;
        this.availabilityPort = availabilityPort;
        this.paymentGatewayPort = paymentGatewayPort;
        this.notificationPort = notificationPort;
    }

    @Transactional
    public BookingResponse createBooking(String touristEmail, CreateBookingRequest request) {
        User tourist = userRepository.findByEmail(touristEmail)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Tourist not found"));

        if (!availabilityPort.isAvailable(request.tourId(), request.bookingDate(), request.startTime())) {
            throw new ApiException(HttpStatus.CONFLICT, "Selected time slot is not available");
        }

        boolean slotOccupied = bookingRepository.existsByTourIdAndBookingDateAndStartTimeAndStatusIn(
                request.tourId(),
                request.bookingDate(),
                request.startTime(),
                ACTIVE_SLOT_STATUSES
        );

        if (slotOccupied) {
            throw new ApiException(HttpStatus.CONFLICT, "Selected time slot is already booked");
        }

        Booking booking = new Booking();
        booking.setTourist(tourist);
        booking.setTourId(request.tourId());
        booking.setBookingDate(request.bookingDate());
        booking.setStartTime(request.startTime());
        booking.setNumPeople(request.numPeople());
        booking.setTotalPrice(request.totalPrice().setScale(2, RoundingMode.HALF_UP));
        booking.setStatus(BookingStatus.CREATED);

        Booking saved = bookingRepository.save(booking);
        return BookingResponse.from(saved);
    }

    public List<BookingResponse> listMyBookings(String touristEmail) {
        return bookingRepository.findByTouristEmailOrderByBookingDateDescStartTimeDesc(touristEmail)
                .stream()
                .map(BookingResponse::from)
                .toList();
    }

    public BookingResponse getMyBooking(String touristEmail, Long bookingId) {
        return BookingResponse.from(findMyBooking(touristEmail, bookingId));
    }

    @Transactional
    public PaymentIntentResponse createPaymentIntent(String touristEmail, CreatePaymentIntentRequest request) {
        Booking booking = findMyBooking(touristEmail, request.bookingId());

        if (booking.getStatus() != BookingStatus.PENDING_PAYMENT
                && booking.getStatus() != BookingStatus.PAYMENT_FAILED) {
            throw new ApiException(HttpStatus.CONFLICT, "Booking is not payable in current status");
        }

        PaymentGatewayPort.PaymentIntentResult intent = paymentGatewayPort.createPaymentIntent(
                booking.getId(),
                booking.getTotalPrice()
        );

        booking.setStripePaymentIntentId(intent.paymentIntentId());
        booking.setStatus(BookingStatus.PENDING_PAYMENT);
        bookingRepository.save(booking);

        Payment payment = paymentRepository.findByBookingId(booking.getId()).orElseGet(Payment::new);
        payment.setBooking(booking);
        payment.setAmount(booking.getTotalPrice());
        payment.setCommission(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
        payment.setGuidePayout(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
        payment.setProviderReference(intent.paymentIntentId());
        payment.setStatus(PaymentStatus.PENDING);
        paymentRepository.save(payment);

        return new PaymentIntentResponse(booking.getId(), intent.paymentIntentId(), intent.clientSecret());
    }

    @Transactional
    public ApiMessageResponse handlePaymentWebhook(PaymentWebhookRequest request) {
        Booking booking = bookingRepository.findByStripePaymentIntentId(request.paymentIntentId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Booking for payment intent not found"));

        Payment payment = paymentRepository.findByBookingId(booking.getId()).orElseGet(Payment::new);
        payment.setBooking(booking);
        payment.setAmount(booking.getTotalPrice());
        payment.setProviderReference(request.paymentIntentId());

        String normalizedStatus = request.status().trim().toUpperCase(Locale.ROOT);
        switch (normalizedStatus) {
            case "SUCCEEDED", "PAYMENT_SUCCEEDED" -> {
                booking.setStatus(BookingStatus.CONFIRMED);
                payment.setStatus(PaymentStatus.SUCCEEDED);
                BigDecimal commission = booking.getTotalPrice()
                        .multiply(COMMISSION_RATE)
                        .setScale(2, RoundingMode.HALF_UP);
                BigDecimal guidePayout = booking.getTotalPrice()
                        .subtract(commission)
                        .setScale(2, RoundingMode.HALF_UP);
                payment.setCommission(commission);
                payment.setGuidePayout(guidePayout);
                notificationPort.sendBookingConfirmed(booking.getTourist().getEmail(), booking.getId());
            }
            case "FAILED", "PAYMENT_FAILED" -> {
                booking.setStatus(BookingStatus.PAYMENT_FAILED);
                payment.setStatus(PaymentStatus.FAILED);
                payment.setCommission(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
                payment.setGuidePayout(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
            }
            default -> throw new ApiException(HttpStatus.BAD_REQUEST, "Unsupported payment webhook status");
        }

        bookingRepository.save(booking);
        paymentRepository.save(payment);
        return new ApiMessageResponse("Webhook processed");
    }

    @Transactional
    public ApiMessageResponse cancelBooking(String touristEmail, Long bookingId) {
        Booking booking = findMyBooking(touristEmail, bookingId);
        if (!isCancellable(booking.getStatus())) {
            throw new ApiException(HttpStatus.CONFLICT, "Booking cannot be cancelled in current status");
        }

        Optional<Payment> paymentOptional = paymentRepository.findByBookingId(booking.getId());
        if (paymentOptional.isPresent()) {
            Payment payment = paymentOptional.get();
            if (booking.getStripePaymentIntentId() != null && !booking.getStripePaymentIntentId().isBlank()) {
                paymentGatewayPort.refundPaymentIntent(booking.getStripePaymentIntentId(), booking.getTotalPrice());
                payment.setStatus(PaymentStatus.REFUNDED);
                paymentRepository.save(payment);
            }
        }

        booking.setStatus(BookingStatus.CANCELLED_BY_TOURIST);
        bookingRepository.save(booking);
        notificationPort.sendBookingCancelled(booking.getTourist().getEmail(), booking.getId());
        return new ApiMessageResponse("Booking cancelled successfully");
    }

    public List<BookingResponse> listGuideBookingRequests(String guideEmail) {
        List<Long> guideTourIds = findGuideTourIds(guideEmail);
        if (guideTourIds.isEmpty()) {
            return List.of();
        }

        return bookingRepository.findByTourIdInOrderByBookingDateDescStartTimeDesc(guideTourIds)
                .stream()
                .map(BookingResponse::from)
                .toList();
    }

    @Transactional
    public BookingResponse acceptBookingRequest(String guideEmail, Long bookingId) {
        Booking booking = findGuideOwnedBooking(guideEmail, bookingId);
        if (booking.getStatus() != BookingStatus.CREATED) {
            throw new ApiException(HttpStatus.CONFLICT, "Only CREATED bookings can be accepted");
        }

        booking.setStatus(BookingStatus.PENDING_PAYMENT);
        Booking saved = bookingRepository.save(booking);
        return BookingResponse.from(saved);
    }

    @Transactional
    public BookingResponse declineBookingRequest(String guideEmail, Long bookingId) {
        Booking booking = findGuideOwnedBooking(guideEmail, bookingId);
        if (!isGuideDeclineAllowed(booking.getStatus())) {
            throw new ApiException(HttpStatus.CONFLICT, "Booking cannot be declined in current status");
        }

        booking.setStatus(BookingStatus.CANCELLED_BY_GUIDE);
        Booking saved = bookingRepository.save(booking);
        return BookingResponse.from(saved);
    }

    private Booking findMyBooking(String touristEmail, Long bookingId) {
        return bookingRepository.findByIdAndTouristEmail(bookingId, touristEmail)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Booking not found"));
    }

    private Booking findGuideOwnedBooking(String guideEmail, Long bookingId) {
        List<Long> guideTourIds = findGuideTourIds(guideEmail);
        if (guideTourIds.isEmpty()) {
            throw new ApiException(HttpStatus.NOT_FOUND, "Guide has no tours");
        }

        return bookingRepository.findByIdAndTourIdIn(bookingId, guideTourIds)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Booking not found for this guide"));
    }

    private List<Long> findGuideTourIds(String guideEmail) {
        User guideUser = userRepository.findByEmail(guideEmail)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Guide user not found"));

        if (guideUser.getRole() != UserRole.GUIDE) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Guide role required");
        }

        Guide guide = guideRepository.findByUserId(guideUser.getId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Guide profile not found"));

        return tourRepository.findByGuideIdOrderByCreatedAtDesc(guide.getId())
                .stream()
                .map(Tour::getId)
                .toList();
    }

    private boolean isCancellable(BookingStatus status) {
        return status == BookingStatus.CREATED
                || status == BookingStatus.PENDING_PAYMENT
                || status == BookingStatus.CONFIRMED
                || status == BookingStatus.PAYMENT_FAILED;
    }

    private boolean isGuideDeclineAllowed(BookingStatus status) {
        return status == BookingStatus.CREATED
                || status == BookingStatus.PENDING_PAYMENT
                || status == BookingStatus.PAYMENT_FAILED;
    }
}
