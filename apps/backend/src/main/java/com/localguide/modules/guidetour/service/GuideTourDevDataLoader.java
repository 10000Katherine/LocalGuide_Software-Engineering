package com.localguide.modules.guidetour.service;

import com.localguide.modules.authuser.domain.User;
import com.localguide.modules.authuser.domain.UserRole;
import com.localguide.modules.authuser.repository.UserRepository;
import com.localguide.modules.guidetour.domain.AvailabilitySlot;
import com.localguide.modules.guidetour.domain.Guide;
import com.localguide.modules.guidetour.domain.GuideVerificationStatus;
import com.localguide.modules.guidetour.domain.Tour;
import com.localguide.modules.guidetour.repository.AvailabilitySlotRepository;
import com.localguide.modules.guidetour.repository.GuideRepository;
import com.localguide.modules.guidetour.repository.TourRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Configuration
public class GuideTourDevDataLoader {

    @Bean
    CommandLineRunner seedGuideTourData(UserRepository userRepository,
                                        GuideRepository guideRepository,
                                        TourRepository tourRepository,
                                        AvailabilitySlotRepository availabilitySlotRepository,
                                        PasswordEncoder passwordEncoder) {
        return args -> {
            seedGuide(userRepository, guideRepository, tourRepository, availabilitySlotRepository, passwordEncoder,
                    "guide@localguide.dev", "Guide", "Demo",
                    "British Columbia", "Vancouver", "Downtown", "English, French",
                    "Friendly local guide specializing in city walks and food experiences.",
                    new BigDecimal("4.80"), 12,
                    List.of(
                            new TourSeed("Vancouver Downtown City Walk", "Explore Gastown, Waterfront, and hidden local spots in downtown Vancouver.", "City", "British Columbia", "Vancouver", "English", new BigDecimal("65.00"), new BigDecimal("2.50"), 8),
                            new TourSeed("Vancouver Food and Market Tour", "Visit local food markets and taste signature dishes with cultural stories.", "Food", "British Columbia", "Vancouver", "English", new BigDecimal("95.00"), new BigDecimal("3.00"), 6)
                    ),
                    List.of(
                            new SlotSeed(LocalDate.now().plusDays(1), LocalTime.of(9, 0), LocalTime.of(11, 30)),
                            new SlotSeed(LocalDate.now().plusDays(2), LocalTime.of(14, 0), LocalTime.of(17, 0))
                    )
            );

            seedGuide(userRepository, guideRepository, tourRepository, availabilitySlotRepository, passwordEncoder,
                    "guide.toronto@localguide.dev", "Aisha", "Patel",
                    "Ontario", "Toronto", "Downtown", "English, Hindi",
                    "Urban storyteller with a focus on multicultural neighborhoods and food culture.",
                    new BigDecimal("4.70"), 19,
                    List.of(
                            new TourSeed("Toronto City Highlights", "Discover Toronto landmarks, local history, and waterfront views.", "City", "Ontario", "Toronto", "English", new BigDecimal("70.00"), new BigDecimal("2.00"), 10),
                            new TourSeed("Kensington Food Journey", "Taste iconic Toronto flavors across a curated market route.", "Food", "Ontario", "Toronto", "English", new BigDecimal("88.00"), new BigDecimal("2.50"), 8)
                    ),
                    List.of(
                            new SlotSeed(LocalDate.now().plusDays(1), LocalTime.of(10, 0), LocalTime.of(12, 0)),
                            new SlotSeed(LocalDate.now().plusDays(3), LocalTime.of(15, 0), LocalTime.of(18, 0))
                    )
            );

            seedGuide(userRepository, guideRepository, tourRepository, availabilitySlotRepository, passwordEncoder,
                    "guide.montreal@localguide.dev", "Julien", "Moreau",
                    "Quebec", "Montreal", "Old Montreal", "French, English",
                    "Bilingual heritage guide with deep knowledge of architecture and old-town history.",
                    new BigDecimal("4.90"), 27,
                    List.of(
                            new TourSeed("Old Montreal Heritage Walk", "Walk through historic plazas, cathedrals, and hidden alleys.", "Historical", "Quebec", "Montreal", "French", new BigDecimal("72.00"), new BigDecimal("2.00"), 9),
                            new TourSeed("Montreal Evening Culture Tour", "Experience local arts, music, and cultural venues in the city center.", "Cultural", "Quebec", "Montreal", "French", new BigDecimal("99.00"), new BigDecimal("3.00"), 7)
                    ),
                    List.of(
                            new SlotSeed(LocalDate.now().plusDays(2), LocalTime.of(9, 30), LocalTime.of(11, 30)),
                            new SlotSeed(LocalDate.now().plusDays(4), LocalTime.of(18, 0), LocalTime.of(21, 0))
                    )
            );

            seedGuide(userRepository, guideRepository, tourRepository, availabilitySlotRepository, passwordEncoder,
                    "guide.calgary@localguide.dev", "Noah", "Campbell",
                    "Alberta", "Calgary", "Beltline", "English, Spanish",
                    "Outdoor-focused guide for nature escapes and adventure routes near Calgary.",
                    new BigDecimal("4.60"), 11,
                    List.of(
                            new TourSeed("Calgary Nature Escape", "Enjoy scenic viewpoints and easy adventure trails near the city.", "Nature", "Alberta", "Calgary", "English", new BigDecimal("82.00"), new BigDecimal("3.50"), 6),
                            new TourSeed("Rocky Foothills Adventure", "A guided adventure experience with panoramic landscapes.", "Adventure", "Alberta", "Calgary", "English", new BigDecimal("120.00"), new BigDecimal("4.50"), 5)
                    ),
                    List.of(
                            new SlotSeed(LocalDate.now().plusDays(2), LocalTime.of(8, 0), LocalTime.of(11, 30)),
                            new SlotSeed(LocalDate.now().plusDays(5), LocalTime.of(13, 30), LocalTime.of(18, 0))
                    )
            );
        };
    }

    private void seedGuide(UserRepository userRepository,
                           GuideRepository guideRepository,
                           TourRepository tourRepository,
                           AvailabilitySlotRepository availabilitySlotRepository,
                           PasswordEncoder passwordEncoder,
                           String email,
                           String firstName,
                           String lastName,
                           String province,
                           String city,
                           String neighborhood,
                           String languages,
                           String bio,
                           BigDecimal avgRating,
                           int totalReviews,
                           List<TourSeed> tours,
                           List<SlotSeed> slots) {
        User guideUser = userRepository.findByEmail(email)
                .orElseGet(() -> createGuideUser(userRepository, passwordEncoder, email, firstName, lastName));

        Guide guide = guideRepository.findByUserId(guideUser.getId())
                .orElseGet(() -> createGuideProfile(guideRepository, guideUser, province, city, neighborhood, languages, bio, avgRating, totalReviews));

        if (tourRepository.findByGuideIdOrderByCreatedAtDesc(guide.getId()).isEmpty()) {
            for (TourSeed seed : tours) {
                createTour(tourRepository, guide, seed);
            }
        }

        if (availabilitySlotRepository.findByGuideIdOrderBySlotDateAscStartTimeAsc(guide.getId()).isEmpty()) {
            for (SlotSeed seed : slots) {
                createSlot(availabilitySlotRepository, guide, seed);
            }
        }
    }

    private User createGuideUser(UserRepository userRepository,
                                 PasswordEncoder passwordEncoder,
                                 String email,
                                 String firstName,
                                 String lastName) {
        User user = new User();
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode("Password123!"));
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setPhone("0000000000");
        user.setRole(UserRole.GUIDE);
        user.setActive(true);
        return userRepository.save(user);
    }

    private Guide createGuideProfile(GuideRepository guideRepository,
                                     User guideUser,
                                     String province,
                                     String city,
                                     String neighborhood,
                                     String languages,
                                     String bio,
                                     BigDecimal avgRating,
                                     int totalReviews) {
        Guide guide = new Guide();
        guide.setUser(guideUser);
        guide.setProvince(province);
        guide.setCity(city);
        guide.setNeighborhood(neighborhood);
        guide.setLanguages(languages);
        guide.setBio(bio);
        guide.setVerificationStatus(GuideVerificationStatus.APPROVED);
        guide.setAvgRating(avgRating);
        guide.setTotalReviews(totalReviews);
        return guideRepository.save(guide);
    }

    private void createTour(TourRepository tourRepository, Guide guide, TourSeed seed) {
        Tour tour = new Tour();
        tour.setGuide(guide);
        tour.setTitle(seed.title());
        tour.setDescription(seed.description());
        tour.setCategory(seed.category());
        tour.setProvince(seed.province());
        tour.setCity(seed.city());
        tour.setLanguage(seed.language());
        tour.setPrice(seed.price());
        tour.setDurationHours(seed.durationHours());
        tour.setGroupSize(seed.groupSize());
        tourRepository.save(tour);
    }

    private void createSlot(AvailabilitySlotRepository availabilitySlotRepository, Guide guide, SlotSeed seed) {
        AvailabilitySlot slot = new AvailabilitySlot();
        slot.setGuide(guide);
        slot.setSlotDate(seed.slotDate());
        slot.setStartTime(seed.startTime());
        slot.setEndTime(seed.endTime());
        availabilitySlotRepository.save(slot);
    }

    private record TourSeed(
            String title,
            String description,
            String category,
            String province,
            String city,
            String language,
            BigDecimal price,
            BigDecimal durationHours,
            Integer groupSize
    ) {
    }

    private record SlotSeed(
            LocalDate slotDate,
            LocalTime startTime,
            LocalTime endTime
    ) {
    }
}
