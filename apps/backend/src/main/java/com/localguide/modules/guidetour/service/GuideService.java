package com.localguide.modules.guidetour.service;

import com.localguide.common.exception.ApiException;
import com.localguide.modules.authuser.domain.User;
import com.localguide.modules.authuser.domain.UserRole;
import com.localguide.modules.authuser.repository.UserRepository;
import com.localguide.modules.guidetour.domain.Guide;
import com.localguide.modules.guidetour.domain.GuideVerificationStatus;
import com.localguide.modules.guidetour.domain.Tour;
import com.localguide.modules.guidetour.dto.CreateTourRequest;
import com.localguide.modules.guidetour.dto.GuideProfileResponse;
import com.localguide.modules.guidetour.dto.GuideSummaryResponse;
import com.localguide.modules.guidetour.dto.PagedResponse;
import com.localguide.modules.guidetour.dto.TourResponse;
import com.localguide.modules.guidetour.dto.UpdateGuideProfileRequest;
import com.localguide.modules.guidetour.repository.GuideRepository;
import com.localguide.modules.guidetour.repository.TourRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class GuideService {

    private static final String DEFAULT_PROVINCE = "";
    private static final String DEFAULT_CITY = "";
    private static final String DEFAULT_LANGUAGE = "";

    private final GuideRepository guideRepository;
    private final UserRepository userRepository;
    private final TourRepository tourRepository;

    public GuideService(GuideRepository guideRepository,
                        UserRepository userRepository,
                        TourRepository tourRepository) {
        this.guideRepository = guideRepository;
        this.userRepository = userRepository;
        this.tourRepository = tourRepository;
    }

    @Transactional(readOnly = true)
    public PagedResponse<GuideSummaryResponse> listGuides(Integer page, Integer size) {
        int normalizedPage = page == null || page < 0 ? 0 : page;
        int normalizedSize = size == null || size <= 0 ? 12 : Math.min(size, 50);
        Pageable pageable = PageRequest.of(normalizedPage, normalizedSize, Sort.by(Sort.Direction.DESC, "id"));
        Page<GuideSummaryResponse> results = guideRepository.findAll(pageable).map(GuideSummaryResponse::from);
        return PagedResponse.fromPage(results);
    }

    @Transactional(readOnly = true)
    public GuideProfileResponse getProfile(Long guideId) {
        Guide guide = requireGuide(guideId);
        return buildGuideProfile(guide);
    }

    @Transactional(readOnly = true)
    public GuideProfileResponse getGuideProfile(Long guideId) {
        return getProfile(guideId);
    }

    @Transactional(readOnly = true)
    public GuideProfileResponse getCurrentGuideProfile(String email) {
        Guide guide = requireCurrentGuide(email);
        return buildGuideProfile(guide);
    }

    @Transactional
    public GuideSummaryResponse updateProfile(String email, UpdateGuideProfileRequest request) {
        Guide guide = requireCurrentGuide(email);

        guide.setProvince(resolveRequired(request.province(), guide.getProvince(), DEFAULT_PROVINCE));
        guide.setCity(resolveRequired(request.city(), guide.getCity(), DEFAULT_CITY));
        guide.setNeighborhood(normalizeOptional(request.neighborhood()));
        guide.setLanguages(resolveRequired(request.language(), guide.getLanguages(), DEFAULT_LANGUAGE));
        guide.setBio(normalizeOptional(request.bio()));
        guide.setPhotoUrl(normalizeOptional(request.photoUrl()));

        Guide saved = guideRepository.save(guide);
        return GuideSummaryResponse.from(saved);
    }

    @Transactional
    public GuideSummaryResponse updateCurrentGuide(String email, UpdateGuideProfileRequest request) {
        return updateProfile(email, request);
    }

    @Transactional
    public TourResponse createTour(String email, CreateTourRequest request) {
        Guide guide = requireCurrentGuide(email);

        Tour tour = new Tour();
        tour.setGuide(guide);
        applyTourRequest(tour, request);

        return TourResponse.from(tourRepository.save(tour));
    }

    @Transactional
    public TourResponse createTour(String email, Long guideId, CreateTourRequest request) {
        Guide currentGuide = requireCurrentGuide(email);
        if (!currentGuide.getId().equals(guideId)) {
            throw new ApiException(HttpStatus.FORBIDDEN, "You can only create tours for your own guide profile");
        }
        return createTour(email, request);
    }

    @Transactional
    public void deleteTour(String email, Long tourId) {
        Guide guide = requireCurrentGuide(email);
        Tour tour = requireOwnedTour(guide.getId(), tourId);
        tourRepository.delete(tour);
    }

    @Transactional(readOnly = true)
    public String getVerificationStatus(String email) {
        Guide guide = requireCurrentGuide(email);
        return guide.getVerificationStatus().name();
    }

    @Transactional
    public Guide requireCurrentGuide(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found"));

        if (user.getRole() != UserRole.GUIDE) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Guide role required");
        }

        Guide existing = guideRepository.findByUserId(user.getId()).orElse(null);
        if (existing != null) {
            return existing;
        }

        try {
            return guideRepository.save(buildDefaultGuide(user));
        } catch (DataIntegrityViolationException exception) {
            // Concurrent requests may try to create the same guide row simultaneously.
            return guideRepository.findByUserId(user.getId())
                    .orElseThrow(() -> new ApiException(HttpStatus.CONFLICT, "Guide profile already exists"));
        }
    }

    @Transactional(readOnly = true)
    public Guide requireGuide(Long guideId) {
        return guideRepository.findById(guideId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Guide not found"));
    }

    private Guide buildDefaultGuide(User user) {
        Guide guide = new Guide();
        guide.setUser(user);
        guide.setProvince(DEFAULT_PROVINCE);
        guide.setCity(DEFAULT_CITY);
        guide.setNeighborhood(null);
        guide.setLanguages(DEFAULT_LANGUAGE);
        guide.setBio(null);
        guide.setPhotoUrl(null);
        guide.setVerificationStatus(GuideVerificationStatus.PENDING);
        guide.setAvgRating(BigDecimal.ZERO.setScale(2));
        guide.setTotalReviews(0);
        return guide;
    }

    private GuideProfileResponse buildGuideProfile(Guide guide) {
        GuideSummaryResponse summary = GuideSummaryResponse.from(guide);
        List<TourResponse> tours = tourRepository.findByGuideIdOrderByCreatedAtDesc(guide.getId())
                .stream()
                .map(TourResponse::from)
                .toList();
        return GuideProfileResponse.of(summary, tours);
    }

    private Tour requireOwnedTour(Long guideId, Long tourId) {
        Tour tour = tourRepository.findById(tourId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Tour not found"));

        if (!tour.getGuide().getId().equals(guideId)) {
            throw new ApiException(HttpStatus.FORBIDDEN, "You can only modify your own tours");
        }
        return tour;
    }

    private void applyTourRequest(Tour tour, CreateTourRequest request) {
        tour.setTitle(request.title().trim());
        tour.setDescription(request.description().trim());
        tour.setCategory(request.category().trim());
        tour.setProvince(request.province().trim());
        tour.setCity(request.city().trim());
        tour.setLanguage(request.language().trim());
        tour.setPrice(request.price());
        tour.setDurationHours(request.durationHours());
        tour.setGroupSize(request.groupSize());
    }

    private String normalizeOptional(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private String resolveRequired(String requested, String current, String fallback) {
        String normalized = normalizeOptional(requested);
        if (normalized != null) {
            return normalized;
        }
        if (current != null && !current.trim().isEmpty()) {
            return current.trim();
        }
        return fallback;
    }
}
