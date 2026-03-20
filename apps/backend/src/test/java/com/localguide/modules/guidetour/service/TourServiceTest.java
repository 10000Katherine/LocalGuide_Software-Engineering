package com.localguide.modules.guidetour.service;

import com.localguide.common.exception.ApiException;
import com.localguide.modules.authuser.domain.User;
import com.localguide.modules.authuser.domain.UserRole;
import com.localguide.modules.authuser.repository.UserRepository;
import com.localguide.modules.guidetour.dto.CreateTourRequest;
import com.localguide.modules.guidetour.dto.TourResponse;
import com.localguide.modules.guidetour.dto.UpdateTourRequest;
import com.localguide.modules.guidetour.repository.GuideRepository;
import com.localguide.modules.guidetour.repository.TourRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
class TourServiceTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GuideRepository guideRepository;

    @Autowired
    private TourRepository tourRepository;

    private TourService tourService;

    @BeforeEach
    void setUp() {
        GuideService guideService = new GuideService(guideRepository, userRepository, tourRepository);
        tourService = new TourService(tourRepository, guideService);
    }

    @Test
    void createTourShouldPersistForGuide() {
        User guideUser = createGuideUser("guide1@local.test", "Guide", "One");

        CreateTourRequest request = new CreateTourRequest(
                "City Walk",
                "A guided tour through downtown",
                "Culture",
                "Ontario",
                "Toronto",
                "English",
                new BigDecimal("80.00"),
                new BigDecimal("2.50"),
                8
        );

        TourResponse created = tourService.createTour(guideUser.getEmail(), request);

        assertEquals("City Walk", created.title());
        assertEquals(guideUser.getId(), created.guideId());
        assertEquals(1, tourRepository.count());
    }

    @Test
    void updateTourShouldBlockDifferentGuide() {
        User owner = createGuideUser("owner@local.test", "Owner", "Guide");
        User other = createGuideUser("other@local.test", "Other", "Guide");

        TourResponse created = tourService.createTour(owner.getEmail(), new CreateTourRequest(
                "Old Title",
                "Old Description",
                "Food",
                "British Columbia",
                "Vancouver",
                "English",
                new BigDecimal("50.00"),
                new BigDecimal("1.50"),
                6
        ));

        ApiException exception = assertThrows(ApiException.class, () ->
                tourService.updateTour(other.getEmail(), created.id(), new UpdateTourRequest(
                        "New Title",
                        "New Description",
                        "Food",
                        "British Columbia",
                        "Vancouver",
                        "English",
                        new BigDecimal("65.00"),
                        new BigDecimal("2.00"),
                        6
                ))
        );

        assertEquals(HttpStatus.FORBIDDEN, exception.getStatus());
    }

    @Test
    void deleteTourShouldRemoveOwnedTour() {
        User guide = createGuideUser("guide2@local.test", "Guide", "Two");

        TourResponse created = tourService.createTour(guide.getEmail(), new CreateTourRequest(
                "Night Tour",
                "City lights and skyline",
                "Sightseeing",
                "Quebec",
                "Montreal",
                "French",
                new BigDecimal("95.00"),
                new BigDecimal("3.00"),
                10
        ));

        tourService.deleteTour(guide.getEmail(), created.id());

        assertEquals(0, tourRepository.count());
    }

    private User createGuideUser(String email, String firstName, String lastName) {
        User user = new User();
        user.setEmail(email);
        user.setPasswordHash("hash");
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setRole(UserRole.GUIDE);
        return userRepository.save(user);
    }
}
