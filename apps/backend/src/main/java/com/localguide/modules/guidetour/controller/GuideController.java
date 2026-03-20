package com.localguide.modules.guidetour.controller;

import com.localguide.modules.guidetour.dto.GuidePhotoUploadResponse;
import com.localguide.modules.guidetour.dto.GuideProfileResponse;
import com.localguide.modules.guidetour.dto.GuideSummaryResponse;
import com.localguide.modules.guidetour.dto.GuideVerificationResponse;
import com.localguide.modules.guidetour.dto.PagedResponse;
import com.localguide.modules.guidetour.dto.UpdateGuideProfileRequest;
import com.localguide.modules.guidetour.dto.AvailabilitySlotResponse;
import com.localguide.modules.guidetour.service.AvailabilityService;
import com.localguide.modules.guidetour.service.GuidePhotoService;
import com.localguide.modules.guidetour.service.GuideService;
import jakarta.validation.Valid;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/guides")
public class GuideController {

    private final GuideService guideService;
    private final GuidePhotoService guidePhotoService;
    private final AvailabilityService availabilityService;

    public GuideController(GuideService guideService,
                           GuidePhotoService guidePhotoService,
                           AvailabilityService availabilityService) {
        this.guideService = guideService;
        this.guidePhotoService = guidePhotoService;
        this.availabilityService = availabilityService;
    }

    @GetMapping
    public PagedResponse<GuideSummaryResponse> listGuides(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size
    ) {
        return guideService.listGuides(page, size);
    }

    @GetMapping("/{id}")
    public GuideProfileResponse getGuideProfile(@PathVariable Long id) {
        return guideService.getProfile(id);
    }

    @GetMapping("/{id}/availability")
    public List<AvailabilitySlotResponse> getGuideAvailability(@PathVariable Long id) {
        return availabilityService.listGuideAvailability(id);
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('GUIDE')")
    public GuideProfileResponse getMyGuideProfile(Authentication authentication) {
        return guideService.getCurrentGuideProfile(authentication.getName());
    }

    @PutMapping("/me")
    @PreAuthorize("hasRole('GUIDE')")
    public GuideSummaryResponse updateMyGuideProfile(Authentication authentication,
                                                     @RequestBody @Valid UpdateGuideProfileRequest request) {
        return guideService.updateProfile(authentication.getName(), request);
    }

    @PostMapping
    @PreAuthorize("hasRole('GUIDE')")
    public GuideSummaryResponse createOrUpdateMyGuideProfile(Authentication authentication,
                                                             @RequestBody @Valid UpdateGuideProfileRequest request) {
        return guideService.updateProfile(authentication.getName(), request);
    }

    @GetMapping("/me/verification")
    @PreAuthorize("hasRole('GUIDE')")
    public GuideVerificationResponse getMyVerification(Authentication authentication) {
        return new GuideVerificationResponse(guideService.getVerificationStatus(authentication.getName()));
    }

    @PostMapping(value = "/me/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('GUIDE')")
    public GuidePhotoUploadResponse uploadMyGuidePhoto(Authentication authentication,
                                                       @RequestParam("file") MultipartFile file) {
        return guidePhotoService.uploadMyPhoto(authentication.getName(), file);
    }

    @GetMapping("/photo/{filename:.+}")
    public ResponseEntity<Resource> getGuidePhoto(@PathVariable String filename) {
        Resource resource = guidePhotoService.loadPhotoResource(filename);
        MediaType contentType = guidePhotoService.resolveContentType(filename);
        return ResponseEntity.ok()
                .contentType(contentType)
                .body(resource);
    }
}
