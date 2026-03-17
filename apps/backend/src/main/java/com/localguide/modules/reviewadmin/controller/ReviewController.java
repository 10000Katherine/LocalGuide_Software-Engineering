package com.localguide.modules.reviewadmin.controller;

import com.localguide.modules.reviewadmin.dto.CreateReviewRequest;
import com.localguide.modules.reviewadmin.dto.RatingSummaryResponse;
import com.localguide.modules.reviewadmin.dto.ReplyReviewRequest;
import com.localguide.modules.reviewadmin.dto.ReviewResponse;
import com.localguide.modules.reviewadmin.service.ReviewService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class ReviewController {
    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping("/reviews")
    @ResponseStatus(HttpStatus.CREATED)
    public ReviewResponse create(Authentication authentication, @RequestBody @Valid CreateReviewRequest request) {
        return reviewService.createReview(authentication.getName(), request);
    }

    @GetMapping("/guides/{guideId}/reviews")
    public List<ReviewResponse> getGuideReviews(@PathVariable Long guideId) {
        return reviewService.getGuideReviews(guideId);
    }

    @GetMapping("/tours/{tourId}/reviews")
    public List<ReviewResponse> getTourReviews(@PathVariable Long tourId) {
        return reviewService.getTourReviews(tourId);
    }

    @PutMapping("/reviews/{reviewId}/reply")
    public ReviewResponse reply(Authentication authentication,
                                @PathVariable Long reviewId,
                                @RequestBody @Valid ReplyReviewRequest request) {
        return reviewService.replyToReview(authentication.getName(), reviewId, request);
    }

    @GetMapping("/guides/{guideId}/ratings/summary")
    public RatingSummaryResponse getGuideRatingSummary(@PathVariable Long guideId) {
        return reviewService.getGuideRatingSummary(guideId);
    }
}
