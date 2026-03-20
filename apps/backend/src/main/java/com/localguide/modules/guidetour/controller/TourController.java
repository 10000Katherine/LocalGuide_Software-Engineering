package com.localguide.modules.guidetour.controller;

import com.localguide.modules.guidetour.dto.CreateTourRequest;
import com.localguide.modules.guidetour.dto.TourResponse;
import com.localguide.modules.guidetour.dto.UpdateTourRequest;
import com.localguide.modules.guidetour.service.TourService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
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
@RequestMapping("/api/v1")
public class TourController {

    private final TourService tourService;

    public TourController(TourService tourService) {
        this.tourService = tourService;
    }

    @PostMapping("/guides/me/tours")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('GUIDE')")
    public TourResponse createTour(Authentication authentication,
                                   @RequestBody @Valid CreateTourRequest request) {
        return tourService.createTour(authentication.getName(), request);
    }

    @GetMapping("/guides/me/tours")
    @PreAuthorize("hasRole('GUIDE')")
    public List<TourResponse> listMyTours(Authentication authentication) {
        return tourService.listMyTours(authentication.getName());
    }

    @GetMapping("/guides/{id}/tours")
    public List<TourResponse> listGuideTours(@PathVariable Long id) {
        return tourService.listToursByGuideId(id);
    }

    @PostMapping("/guides/{id}/tours")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('GUIDE')")
    public TourResponse createTourForGuide(Authentication authentication,
                                           @PathVariable Long id,
                                           @RequestBody @Valid CreateTourRequest request) {
        return tourService.createTour(authentication.getName(), id, request);
    }

    @GetMapping("/tours/{id}")
    public TourResponse getTourById(@PathVariable Long id) {
        return tourService.getTourById(id);
    }

    @PutMapping("/tours/{id}")
    @PreAuthorize("hasRole('GUIDE')")
    public TourResponse updateTour(Authentication authentication,
                                   @PathVariable Long id,
                                   @RequestBody @Valid UpdateTourRequest request) {
        return tourService.updateTour(authentication.getName(), id, request);
    }

    @DeleteMapping("/tours/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('GUIDE')")
    public void deleteTour(Authentication authentication, @PathVariable Long id) {
        tourService.deleteTour(authentication.getName(), id);
    }
}
