package com.localguide.modules.reviewadmin.service;

import com.localguide.common.exception.ApiException;
import com.localguide.modules.authuser.domain.User;
import com.localguide.modules.authuser.domain.UserRole;
import com.localguide.modules.authuser.repository.UserRepository;
import com.localguide.modules.reviewadmin.domain.Review;
import com.localguide.modules.reviewadmin.dto.CreateReviewRequest;
import com.localguide.modules.reviewadmin.dto.RatingSummaryResponse;
import com.localguide.modules.reviewadmin.dto.ReplyReviewRequest;
import com.localguide.modules.reviewadmin.dto.ReviewResponse;
import com.localguide.modules.reviewadmin.repository.FavoriteRepository;
import com.localguide.modules.reviewadmin.repository.ReviewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private FavoriteRepository favoriteRepository;

    @Mock
    private UserRepository userRepository;

    private ReviewService reviewService;

    @BeforeEach
    void setUp() {
        reviewService = new ReviewService(reviewRepository, favoriteRepository, userRepository);
    }

    @Test
    void createReviewShouldPersistReviewForTourist() {
        User tourist = createUser(10L, "tourist@example.com", UserRole.TOURIST);
        User guide = createUser(20L, "guide@example.com", UserRole.GUIDE);
        CreateReviewRequest request = new CreateReviewRequest("BOOK-123", guide.getId(), 7L, 5, "Amazing trip");

        when(userRepository.findByEmail("tourist@example.com")).thenReturn(Optional.of(tourist));
        when(reviewRepository.findByBookingReference("BOOK-123")).thenReturn(Optional.empty());
        when(userRepository.findById(20L)).thenReturn(Optional.of(guide));
        when(reviewRepository.save(any(Review.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ReviewResponse response = reviewService.createReview("tourist@example.com", request);

        assertEquals("BOOK-123", response.bookingReference());
        assertEquals(5, response.rating());
        assertEquals(20L, response.guideId());
    }

    @Test
    void createReviewShouldRejectDuplicateBookingReference() {
        User tourist = createUser(10L, "tourist@example.com", UserRole.TOURIST);
        Review existing = new Review();
        existing.setBookingReference("BOOK-123");

        when(userRepository.findByEmail("tourist@example.com")).thenReturn(Optional.of(tourist));
        when(reviewRepository.findByBookingReference("BOOK-123")).thenReturn(Optional.of(existing));

        ApiException exception = assertThrows(ApiException.class,
                () -> reviewService.createReview("tourist@example.com", new CreateReviewRequest("BOOK-123", 20L, null, 4, "Good")));

        assertEquals(HttpStatus.CONFLICT, exception.getStatus());
    }

    @Test
    void replyShouldFailWhenGuideDoesNotOwnReview() {
        User guide = createUser(20L, "guide@example.com", UserRole.GUIDE);
        User otherGuide = createUser(21L, "other-guide@example.com", UserRole.GUIDE);
        User tourist = createUser(10L, "tourist@example.com", UserRole.TOURIST);

        Review review = new Review();
        review.setGuide(otherGuide);
        review.setTourist(tourist);
        review.setComment("Nice");
        review.setBookingReference("BOOK-1");
        review.setRating(4);

        when(userRepository.findByEmail("guide@example.com")).thenReturn(Optional.of(guide));
        when(reviewRepository.findById(99L)).thenReturn(Optional.of(review));

        ApiException exception = assertThrows(ApiException.class,
                () -> reviewService.replyToReview("guide@example.com", 99L, new ReplyReviewRequest("Thanks")));

        assertEquals(HttpStatus.FORBIDDEN, exception.getStatus());
    }

    @Test
    void getGuideRatingSummaryShouldReturnAverageAndFavorites() {
        Review review1 = new Review();
        review1.setRating(5);
        Review review2 = new Review();
        review2.setRating(3);

        when(reviewRepository.findByGuideIdOrderByCreatedAtDesc(20L)).thenReturn(List.of(review1, review2));
        when(favoriteRepository.countByGuideId(20L)).thenReturn(7L);

        RatingSummaryResponse response = reviewService.getGuideRatingSummary(20L);

        assertEquals(4.0, response.averageRating());
        assertEquals(2, response.totalReviews());
        assertEquals(7, response.favoriteCount());
    }

    private User createUser(Long id, String email, UserRole role) {
        User user = new User();
        try {
            java.lang.reflect.Field field = User.class.getDeclaredField("id");
            field.setAccessible(true);
            field.set(user, id);
        } catch (Exception ignored) {
        }
        user.setEmail(email);
        user.setFirstName("Test");
        user.setLastName("User");
        user.setRole(role);
        user.setActive(true);
        return user;
    }
}
