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
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final FavoriteRepository favoriteRepository;
    private final UserRepository userRepository;

    public ReviewService(ReviewRepository reviewRepository, FavoriteRepository favoriteRepository, UserRepository userRepository) {
        this.reviewRepository = reviewRepository;
        this.favoriteRepository = favoriteRepository;
        this.userRepository = userRepository;
    }

    public ReviewResponse createReview(String touristEmail, CreateReviewRequest request) {
        User tourist = getRequiredUserByEmail(touristEmail);
        requireRole(tourist, UserRole.TOURIST, "Only tourists can create reviews");

        if (reviewRepository.findByBookingReference(request.bookingReference()).isPresent()) {
            throw new ApiException(HttpStatus.CONFLICT, "A review already exists for this booking reference");
        }

        User guide = getRequiredUserById(request.guideId());
        requireRole(guide, UserRole.GUIDE, "Selected user is not a guide");

        Review review = new Review();
        review.setBookingReference(request.bookingReference().trim());
        review.setTourist(tourist);
        review.setGuide(guide);
        review.setTourId(request.tourId());
        review.setRating(request.rating());
        review.setComment(request.comment().trim());

        return ReviewResponse.from(reviewRepository.save(review));
    }

    public List<ReviewResponse> getGuideReviews(Long guideId) {
        return reviewRepository.findByGuideIdOrderByCreatedAtDesc(guideId)
                .stream()
                .map(ReviewResponse::from)
                .toList();
    }

    public List<ReviewResponse> getTourReviews(Long tourId) {
        return reviewRepository.findByTourIdOrderByCreatedAtDesc(tourId)
                .stream()
                .map(ReviewResponse::from)
                .toList();
    }

    public ReviewResponse replyToReview(String guideEmail, Long reviewId, ReplyReviewRequest request) {
        User guide = getRequiredUserByEmail(guideEmail);
        requireRole(guide, UserRole.GUIDE, "Only guides can reply to reviews");

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Review not found"));

        if (!review.getGuide().getId().equals(guide.getId())) {
            throw new ApiException(HttpStatus.FORBIDDEN, "You can only reply to reviews left for your profile");
        }

        review.setGuideReply(request.reply().trim());
        return ReviewResponse.from(reviewRepository.save(review));
    }

    public RatingSummaryResponse getGuideRatingSummary(Long guideId) {
        List<Review> reviews = reviewRepository.findByGuideIdOrderByCreatedAtDesc(guideId);
        double average = reviews.stream().mapToInt(Review::getRating).average().orElse(0.0);
        long favorites = favoriteRepository.countByGuideId(guideId);
        return new RatingSummaryResponse(guideId, Math.round(average * 100.0) / 100.0, reviews.size(), favorites);
    }

    public GuideEarningsProjection getGuideEarnings(Long guideId) {
        RatingSummaryResponse summary = getGuideRatingSummary(guideId);
        User guide = getRequiredUserById(guideId);
        return new GuideEarningsProjection(
                guide.getId(),
                guide.getFirstName() + " " + guide.getLastName(),
                summary.totalReviews(),
                summary.favoriteCount(),
                summary.averageRating()
        );
    }

    private User getRequiredUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found"));
    }

    private User getRequiredUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found"));
    }

    private void requireRole(User user, UserRole expected, String message) {
        if (user.getRole() != expected) {
            throw new ApiException(HttpStatus.FORBIDDEN, message);
        }
    }

    public record GuideEarningsProjection(Long guideId, String guideName, long totalReviews, long totalFavorites, double averageRating) {
    }
}
