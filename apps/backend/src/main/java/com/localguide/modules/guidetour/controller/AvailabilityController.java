package com.localguide.modules.guidetour.controller;

import com.localguide.modules.guidetour.dto.AvailabilitySlotRequest;
import com.localguide.modules.guidetour.dto.AvailabilitySlotResponse;
import com.localguide.modules.guidetour.service.AvailabilityService;
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
@RequestMapping("/api/v1/guides/me/availability")
@PreAuthorize("hasRole('GUIDE')")
public class AvailabilityController {

    private final AvailabilityService availabilityService;

    public AvailabilityController(AvailabilityService availabilityService) {
        this.availabilityService = availabilityService;
    }

    @GetMapping
    public List<AvailabilitySlotResponse> listMyAvailability(Authentication authentication) {
        return availabilityService.listMyAvailability(authentication.getName());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AvailabilitySlotResponse createAvailability(Authentication authentication,
                                                       @RequestBody @Valid AvailabilitySlotRequest request) {
        return availabilityService.createAvailability(authentication.getName(), request);
    }

    @PutMapping("/{id}")
    public AvailabilitySlotResponse updateAvailability(Authentication authentication,
                                                       @PathVariable Long id,
                                                       @RequestBody @Valid AvailabilitySlotRequest request) {
        return availabilityService.updateAvailability(authentication.getName(), id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAvailability(Authentication authentication, @PathVariable Long id) {
        availabilityService.deleteAvailability(authentication.getName(), id);
    }
}
