package com.localguide.modules.guidetour.service;

import com.localguide.common.exception.ApiException;
import com.localguide.modules.guidetour.domain.Guide;
import com.localguide.modules.guidetour.domain.Tour;
import com.localguide.modules.guidetour.dto.CreateTourRequest;
import com.localguide.modules.guidetour.dto.TourResponse;
import com.localguide.modules.guidetour.dto.UpdateTourRequest;
import com.localguide.modules.guidetour.repository.TourRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TourService {

    private final TourRepository tourRepository;
    private final GuideService guideService;

    public TourService(TourRepository tourRepository, GuideService guideService) {
        this.tourRepository = tourRepository;
        this.guideService = guideService;
    }

    @Transactional
    public TourResponse createTour(String email, CreateTourRequest request) {
        return guideService.createTour(email, request);
    }

    @Transactional
    public TourResponse createTour(String email, Long guideId, CreateTourRequest request) {
        return guideService.createTour(email, guideId, request);
    }

    @Transactional(readOnly = true)
    public List<TourResponse> listMyTours(String email) {
        Guide guide = guideService.requireCurrentGuide(email);
        return tourRepository.findByGuideIdOrderByCreatedAtDesc(guide.getId())
                .stream()
                .map(TourResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<TourResponse> listToursByGuideId(Long guideId) {
        guideService.requireGuide(guideId);
        return tourRepository.findByGuideIdOrderByCreatedAtDesc(guideId)
                .stream()
                .map(TourResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public TourResponse getTourById(Long id) {
        Tour tour = tourRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Tour not found"));
        return TourResponse.from(tour);
    }

    @Transactional
    public TourResponse updateTour(String email, Long tourId, UpdateTourRequest request) {
        Guide guide = guideService.requireCurrentGuide(email);
        Tour tour = requireOwnedTour(guide.getId(), tourId);

        applyRequest(tour, request);

        return TourResponse.from(tourRepository.save(tour));
    }

    @Transactional
    public void deleteTour(String email, Long tourId) {
        guideService.deleteTour(email, tourId);
    }

    private Tour requireOwnedTour(Long guideId, Long tourId) {
        Tour tour = tourRepository.findById(tourId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Tour not found"));

        if (!tour.getGuide().getId().equals(guideId)) {
            throw new ApiException(HttpStatus.FORBIDDEN, "You can only modify your own tours");
        }
        return tour;
    }

    private void applyRequest(Tour tour, UpdateTourRequest request) {
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
}
