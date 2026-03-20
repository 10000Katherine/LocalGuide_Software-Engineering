package com.localguide.modules.guidetour.service;

import com.localguide.modules.authuser.domain.User;
import com.localguide.modules.authuser.domain.UserRole;
import com.localguide.modules.authuser.repository.UserRepository;
import com.localguide.modules.guidetour.domain.Guide;
import com.localguide.modules.guidetour.domain.GuideVerificationStatus;
import com.localguide.modules.guidetour.domain.Tour;
import com.localguide.modules.guidetour.dto.PagedResponse;
import com.localguide.modules.guidetour.dto.TourResponse;
import com.localguide.modules.guidetour.repository.AvailabilitySlotRepository;
import com.localguide.modules.guidetour.repository.GuideRepository;
import com.localguide.modules.guidetour.repository.TourRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class SearchServiceTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GuideRepository guideRepository;

    @Autowired
    private TourRepository tourRepository;

    @Autowired
    private AvailabilitySlotRepository availabilitySlotRepository;

    private SearchService searchService;

    @BeforeEach
    void setUp() {
        searchService = new SearchService(guideRepository, tourRepository);
        seedData();
    }

    @Test
    void searchToursShouldFilterByCityCategoryAndPrice() {
        PagedResponse<TourResponse> result = searchService.searchTours(
                "Ontario",
                "toronto",
                null,
                "english",
                "culture",
                new BigDecimal("40"),
                new BigDecimal("100"),
                0,
                12
        );

        assertEquals(1, result.totalItems());
        assertEquals("Toronto Highlights", result.items().get(0).title());
    }

    @Test
    void searchGuidesShouldSupportPaginationAndTourFilters() {
        PagedResponse<?> firstPage = searchService.searchGuides(
                "Ontario",
                null,
                null,
                "english",
                "culture",
                new BigDecimal("0"),
                new BigDecimal("120"),
                0,
                1
        );

        assertEquals(1, firstPage.size());
        assertEquals(2, firstPage.totalPages());
        assertEquals(2, firstPage.totalItems());
    }

    @Test
    void searchGuidesShouldFilterByAvailabilityDate() {
        PagedResponse<?> result = searchService.searchGuides(
                null,
                null,
                LocalDate.of(2026, 3, 20),
                null,
                null,
                null,
                null,
                0,
                12
        );

        assertEquals(1, result.totalItems());
    }

    private void seedData() {
        User guideOne = createGuideUser("guide-one@test.local", "Alex", "Stone");
        User guideTwo = createGuideUser("guide-two@test.local", "Jamie", "River");

        Guide g1 = createGuide(guideOne, "Ontario", "Toronto", "English", "City storyteller");
        Guide g2 = createGuide(guideTwo, "Ontario", "Ottawa", "English", "Museum specialist");

        tourRepository.save(createTour(g1, "Toronto Highlights", "Culture", "Ontario", "Toronto", "English", "75.00"));
        tourRepository.save(createTour(g2, "History Walk", "Culture", "Ontario", "Ottawa", "English", "65.00"));
        tourRepository.save(createTour(g2, "Food Crawl", "Food", "Ontario", "Ottawa", "English", "150.00"));
        availabilitySlotRepository.save(createSlot(g1, LocalDate.of(2026, 3, 20)));
        availabilitySlotRepository.save(createSlot(g2, LocalDate.of(2026, 3, 22)));
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

    private Guide createGuide(User user, String province, String city, String language, String bio) {
        Guide guide = new Guide();
        guide.setUser(user);
        guide.setProvince(province);
        guide.setCity(city);
        guide.setNeighborhood(null);
        guide.setLanguages(language);
        guide.setVerificationStatus(GuideVerificationStatus.PENDING);
        guide.setAvgRating(new BigDecimal("0.00"));
        guide.setTotalReviews(0);
        guide.setBio(bio);
        return guideRepository.save(guide);
    }

    private Tour createTour(Guide guide,
                            String title,
                            String category,
                            String province,
                            String city,
                            String language,
                            String price) {
        Tour tour = new Tour();
        tour.setGuide(guide);
        tour.setTitle(title);
        tour.setDescription(title + " description");
        tour.setCategory(category);
        tour.setProvince(province);
        tour.setCity(city);
        tour.setLanguage(language);
        tour.setPrice(new BigDecimal(price));
        tour.setDurationHours(new BigDecimal("2.0"));
        tour.setGroupSize(8);
        return tour;
    }

    private com.localguide.modules.guidetour.domain.AvailabilitySlot createSlot(Guide guide, LocalDate date) {
        com.localguide.modules.guidetour.domain.AvailabilitySlot slot =
                new com.localguide.modules.guidetour.domain.AvailabilitySlot();
        slot.setGuide(guide);
        slot.setSlotDate(date);
        slot.setStartTime(LocalTime.of(9, 0));
        slot.setEndTime(LocalTime.of(11, 0));
        return slot;
    }
}
