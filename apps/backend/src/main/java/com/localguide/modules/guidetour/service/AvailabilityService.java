package com.localguide.modules.guidetour.service;

import com.localguide.common.exception.ApiException;
import com.localguide.modules.guidetour.domain.AvailabilitySlot;
import com.localguide.modules.guidetour.domain.Guide;
import com.localguide.modules.guidetour.dto.AvailabilitySlotRequest;
import com.localguide.modules.guidetour.dto.AvailabilitySlotResponse;
import com.localguide.modules.guidetour.repository.AvailabilitySlotRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
public class AvailabilityService {

    private final AvailabilitySlotRepository availabilitySlotRepository;
    private final GuideService guideService;

    public AvailabilityService(AvailabilitySlotRepository availabilitySlotRepository, GuideService guideService) {
        this.availabilitySlotRepository = availabilitySlotRepository;
        this.guideService = guideService;
    }

    @Transactional(readOnly = true)
    public List<AvailabilitySlotResponse> listGuideAvailability(Long guideId) {
        guideService.requireGuide(guideId);
        return availabilitySlotRepository.findByGuideIdOrderBySlotDateAscStartTimeAsc(guideId)
                .stream()
                .map(AvailabilitySlotResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AvailabilitySlotResponse> listMyAvailability(String email) {
        Guide guide = guideService.requireCurrentGuide(email);
        return availabilitySlotRepository.findByGuideIdOrderBySlotDateAscStartTimeAsc(guide.getId())
                .stream()
                .map(AvailabilitySlotResponse::from)
                .toList();
    }

    @Transactional
    public List<AvailabilitySlotResponse> setSlots(String email, List<AvailabilitySlotRequest> requests) {
        Guide guide = guideService.requireCurrentGuide(email);

        availabilitySlotRepository.deleteByGuideId(guide.getId());
        List<AvailabilitySlot> saved = requests.stream().map(request -> {
            validateTimes(request);
            AvailabilitySlot slot = new AvailabilitySlot();
            slot.setGuide(guide);
            slot.setSlotDate(request.slotDate());
            slot.setStartTime(request.startTime());
            slot.setEndTime(request.endTime());
            return slot;
        }).toList();

        return availabilitySlotRepository.saveAll(saved)
                .stream()
                .map(AvailabilitySlotResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public boolean checkAvailability(Long guideId, LocalDate slotDate, LocalTime startTime, LocalTime endTime) {
        return availabilitySlotRepository.existsByGuideIdAndSlotDateAndStartTimeLessThanEqualAndEndTimeGreaterThanEqual(
                guideId,
                slotDate,
                startTime,
                endTime
        );
    }

    @Transactional
    public AvailabilitySlotResponse createAvailability(String email, AvailabilitySlotRequest request) {
        Guide guide = guideService.requireCurrentGuide(email);
        validateTimes(request);

        AvailabilitySlot slot = new AvailabilitySlot();
        slot.setGuide(guide);
        slot.setSlotDate(request.slotDate());
        slot.setStartTime(request.startTime());
        slot.setEndTime(request.endTime());

        try {
            return AvailabilitySlotResponse.from(availabilitySlotRepository.save(slot));
        } catch (DataIntegrityViolationException exception) {
            throw new ApiException(HttpStatus.CONFLICT, "Duplicate availability slot");
        }
    }

    @Transactional
    public AvailabilitySlotResponse updateAvailability(String email, Long slotId, AvailabilitySlotRequest request) {
        Guide guide = guideService.requireCurrentGuide(email);
        validateTimes(request);

        AvailabilitySlot slot = availabilitySlotRepository.findById(slotId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Availability slot not found"));

        if (!slot.getGuide().getId().equals(guide.getId())) {
            throw new ApiException(HttpStatus.FORBIDDEN, "You can only edit your own availability");
        }

        slot.setSlotDate(request.slotDate());
        slot.setStartTime(request.startTime());
        slot.setEndTime(request.endTime());

        try {
            return AvailabilitySlotResponse.from(availabilitySlotRepository.save(slot));
        } catch (DataIntegrityViolationException exception) {
            throw new ApiException(HttpStatus.CONFLICT, "Duplicate availability slot");
        }
    }

    @Transactional
    public void deleteAvailability(String email, Long slotId) {
        Guide guide = guideService.requireCurrentGuide(email);

        AvailabilitySlot slot = availabilitySlotRepository.findById(slotId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Availability slot not found"));

        if (!slot.getGuide().getId().equals(guide.getId())) {
            throw new ApiException(HttpStatus.FORBIDDEN, "You can only delete your own availability");
        }

        availabilitySlotRepository.delete(slot);
    }

    private void validateTimes(AvailabilitySlotRequest request) {
        LocalDate today = LocalDate.now();
        if (request.slotDate().isBefore(today)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Past dates are not allowed");
        }
        if (request.slotDate().isEqual(today) && request.startTime().isBefore(LocalTime.now())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Past times are not allowed for today");
        }
        if (!request.endTime().isAfter(request.startTime())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "End time must be after start time");
        }
    }
}
