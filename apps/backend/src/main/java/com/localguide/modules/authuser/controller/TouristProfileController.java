package com.localguide.modules.authuser.controller;

import com.localguide.modules.authuser.dto.UpdateTouristProfileRequest;
import com.localguide.modules.authuser.dto.UserResponse;
import com.localguide.modules.authuser.service.TouristProfileService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/tourists/me")
public class TouristProfileController {

    private final TouristProfileService touristProfileService;

    public TouristProfileController(TouristProfileService touristProfileService) {
        this.touristProfileService = touristProfileService;
    }

    @GetMapping
    public UserResponse getCurrentUser(Authentication authentication) {
        return touristProfileService.getByEmail(authentication.getName());
    }

    @PutMapping
    public UserResponse updateCurrentUser(Authentication authentication,
                                          @RequestBody @Valid UpdateTouristProfileRequest request) {
        return touristProfileService.updateByEmail(authentication.getName(), request);
    }
}
