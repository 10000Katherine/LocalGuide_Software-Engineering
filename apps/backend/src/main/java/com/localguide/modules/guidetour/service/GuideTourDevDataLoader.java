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

            seedGuide(userRepository, guideRepository, tourRepository, availabilitySlotRepository, passwordEncoder,
                    "guide.ottawa@localguide.dev", "Sophie", "Nguyen",
                    "Ontario", "Ottawa", "ByWard Market", "English, French",
                    "Capital city host focused on parliament history, museums, and riverside routes.",
                    new BigDecimal("4.75"), 16,
                    List.of(
                            new TourSeed("Ottawa Parliament Walk", "A guided walk through Parliament Hill and key national landmarks.", "Historical", "Ontario", "Ottawa", "English", new BigDecimal("68.00"), new BigDecimal("2.00"), 10),
                            new TourSeed("ByWard Food and Stories", "Taste local classics while learning stories from old Ottawa neighborhoods.", "Food", "Ontario", "Ottawa", "French", new BigDecimal("92.00"), new BigDecimal("2.50"), 8)
                    ),
                    List.of(
                            new SlotSeed(LocalDate.now().plusDays(1), LocalTime.of(11, 0), LocalTime.of(13, 0)),
                            new SlotSeed(LocalDate.now().plusDays(4), LocalTime.of(16, 0), LocalTime.of(18, 30))
                    )
            );

            seedGuide(userRepository, guideRepository, tourRepository, availabilitySlotRepository, passwordEncoder,
                    "guide.edmonton@localguide.dev", "Liam", "Rossi",
                    "Alberta", "Edmonton", "Downtown", "English, Italian",
                    "Festival-season specialist with local routes for arts, parks, and river valley trails.",
                    new BigDecimal("4.65"), 14,
                    List.of(
                            new TourSeed("Edmonton River Valley Explorer", "Walk scenic trails with photo stops and local nature insights.", "Nature", "Alberta", "Edmonton", "English", new BigDecimal("74.00"), new BigDecimal("2.50"), 9),
                            new TourSeed("Arts District Discovery", "Explore galleries, murals, and live local creative spaces.", "Cultural", "Alberta", "Edmonton", "English", new BigDecimal("84.00"), new BigDecimal("2.00"), 7)
                    ),
                    List.of(
                            new SlotSeed(LocalDate.now().plusDays(2), LocalTime.of(9, 0), LocalTime.of(11, 30)),
                            new SlotSeed(LocalDate.now().plusDays(6), LocalTime.of(14, 30), LocalTime.of(17, 0))
                    )
            );

            seedGuide(userRepository, guideRepository, tourRepository, availabilitySlotRepository, passwordEncoder,
                    "guide.winnipeg@localguide.dev", "Maya", "Singh",
                    "Manitoba", "Winnipeg", "Exchange District", "English, Punjabi",
                    "Community-focused guide for heritage buildings, markets, and winter-friendly city routes.",
                    new BigDecimal("4.55"), 10,
                    List.of(
                            new TourSeed("Exchange District Heritage Tour", "Discover iconic architecture and stories from old Winnipeg.", "Historical", "Manitoba", "Winnipeg", "English", new BigDecimal("66.00"), new BigDecimal("2.00"), 10),
                            new TourSeed("Winnipeg Local Flavors Route", "A relaxed tasting route featuring independent local kitchens.", "Food", "Manitoba", "Winnipeg", "English", new BigDecimal("86.00"), new BigDecimal("2.50"), 8)
                    ),
                    List.of(
                            new SlotSeed(LocalDate.now().plusDays(3), LocalTime.of(10, 0), LocalTime.of(12, 0)),
                            new SlotSeed(LocalDate.now().plusDays(7), LocalTime.of(15, 0), LocalTime.of(17, 30))
                    )
            );

            seedGuide(userRepository, guideRepository, tourRepository, availabilitySlotRepository, passwordEncoder,
                    "guide.halifax@localguide.dev", "Emma", "MacLeod",
                    "Nova Scotia", "Halifax", "Waterfront", "English",
                    "Coastal storyteller offering harbor history, maritime culture, and relaxed boardwalk tours.",
                    new BigDecimal("4.85"), 22,
                    List.of(
                            new TourSeed("Halifax Harbor Heritage Walk", "Learn maritime history along the waterfront and citadel area.", "Historical", "Nova Scotia", "Halifax", "English", new BigDecimal("71.00"), new BigDecimal("2.00"), 9),
                            new TourSeed("Seaside Culture Evening", "An evening route with local music spots and ocean-view highlights.", "Cultural", "Nova Scotia", "Halifax", "English", new BigDecimal("98.00"), new BigDecimal("3.00"), 7)
                    ),
                    List.of(
                            new SlotSeed(LocalDate.now().plusDays(2), LocalTime.of(13, 0), LocalTime.of(15, 0)),
                            new SlotSeed(LocalDate.now().plusDays(5), LocalTime.of(18, 0), LocalTime.of(21, 0))
                    )
            );

            seedGuide(userRepository, guideRepository, tourRepository, availabilitySlotRepository, passwordEncoder,
                    "guide.victoria@localguide.dev", "Daniel", "Kim",
                    "British Columbia", "Victoria", "Inner Harbour", "English, Korean",
                    "Garden-city guide combining waterfront walks with hidden local neighborhoods.",
                    new BigDecimal("4.78"), 18,
                    List.of(
                            new TourSeed("Victoria Harbour and Gardens", "A balanced route through harbor views and famous garden districts.", "City", "British Columbia", "Victoria", "English", new BigDecimal("76.00"), new BigDecimal("2.50"), 8),
                            new TourSeed("Old Town Culinary Walk", "A culinary-focused stroll through historic streets and local cafes.", "Food", "British Columbia", "Victoria", "English", new BigDecimal("94.00"), new BigDecimal("3.00"), 7)
                    ),
                    List.of(
                            new SlotSeed(LocalDate.now().plusDays(1), LocalTime.of(8, 30), LocalTime.of(11, 0)),
                            new SlotSeed(LocalDate.now().plusDays(6), LocalTime.of(14, 0), LocalTime.of(17, 0))
                    )
            );

            seedGuide(userRepository, guideRepository, tourRepository, availabilitySlotRepository, passwordEncoder,
                    "guide.quebeccity@localguide.dev", "Camille", "Tremblay",
                    "Quebec", "Quebec City", "Old Quebec", "French, English",
                    "Old Quebec specialist offering architecture routes and local culture experiences.",
                    new BigDecimal("4.88"), 24,
                    List.of(
                            new TourSeed("Old Quebec Fortifications Tour", "Walk UNESCO-listed fortifications with deep local context.", "Historical", "Quebec", "Quebec City", "French", new BigDecimal("79.00"), new BigDecimal("2.50"), 9),
                            new TourSeed("Quebec Cultural Highlights", "Discover artisan lanes, viewpoints, and cultural landmarks.", "Cultural", "Quebec", "Quebec City", "French", new BigDecimal("101.00"), new BigDecimal("3.00"), 7)
                    ),
                    List.of(
                            new SlotSeed(LocalDate.now().plusDays(3), LocalTime.of(9, 30), LocalTime.of(12, 0)),
                            new SlotSeed(LocalDate.now().plusDays(8), LocalTime.of(16, 30), LocalTime.of(19, 30))
                    )
            );

            seedGuide(userRepository, guideRepository, tourRepository, availabilitySlotRepository, passwordEncoder,
                    "guide.saskatoon@localguide.dev", "Olivia", "Chen",
                    "Saskatchewan", "Saskatoon", "Riversdale", "English, Mandarin",
                    "River-city guide focused on art districts and prairie culture routes.",
                    new BigDecimal("4.58"), 9,
                    List.of(
                            new TourSeed("Saskatoon Riverfront Walk", "A scenic route with architecture, bridges, and local stories.", "City", "Saskatchewan", "Saskatoon", "English", new BigDecimal("63.00"), new BigDecimal("2.00"), 10),
                            new TourSeed("Prairie Food Discovery", "Try local prairie-inspired menus and community markets.", "Food", "Saskatchewan", "Saskatoon", "English", new BigDecimal("81.00"), new BigDecimal("2.50"), 8)
                    ),
                    List.of(
                            new SlotSeed(LocalDate.now().plusDays(2), LocalTime.of(10, 30), LocalTime.of(12, 30)),
                            new SlotSeed(LocalDate.now().plusDays(6), LocalTime.of(15, 0), LocalTime.of(17, 30))
                    )
            );

            seedGuide(userRepository, guideRepository, tourRepository, availabilitySlotRepository, passwordEncoder,
                    "guide.regina@localguide.dev", "Ethan", "Brooks",
                    "Saskatchewan", "Regina", "Cathedral", "English",
                    "Regina local host with curated city-history and lakeside experience tours.",
                    new BigDecimal("4.52"), 8,
                    List.of(
                            new TourSeed("Regina Heritage Loop", "Walk through key historical landmarks and local civic architecture.", "Historical", "Saskatchewan", "Regina", "English", new BigDecimal("61.00"), new BigDecimal("2.00"), 10),
                            new TourSeed("Wascana Lake Experience", "Relaxed route around parks, viewpoints, and local stories.", "Nature", "Saskatchewan", "Regina", "English", new BigDecimal("79.00"), new BigDecimal("2.50"), 8)
                    ),
                    List.of(
                            new SlotSeed(LocalDate.now().plusDays(1), LocalTime.of(9, 0), LocalTime.of(11, 0)),
                            new SlotSeed(LocalDate.now().plusDays(7), LocalTime.of(14, 0), LocalTime.of(16, 30))
                    )
            );

            seedGuide(userRepository, guideRepository, tourRepository, availabilitySlotRepository, passwordEncoder,
                    "guide.stjohns@localguide.dev", "Harper", "O'Neill",
                    "Newfoundland and Labrador", "St. John's", "Downtown", "English",
                    "East-coast guide bringing harbor history and colorful street stories to life.",
                    new BigDecimal("4.83"), 15,
                    List.of(
                            new TourSeed("St. John's Harbor History", "Explore harbor landmarks and historic neighborhoods by foot.", "Historical", "Newfoundland and Labrador", "St. John's", "English", new BigDecimal("73.00"), new BigDecimal("2.50"), 9),
                            new TourSeed("Signal Hill and Coastal Views", "A guided route with dramatic viewpoints and local legends.", "Nature", "Newfoundland and Labrador", "St. John's", "English", new BigDecimal("97.00"), new BigDecimal("3.00"), 7)
                    ),
                    List.of(
                            new SlotSeed(LocalDate.now().plusDays(3), LocalTime.of(8, 30), LocalTime.of(11, 0)),
                            new SlotSeed(LocalDate.now().plusDays(8), LocalTime.of(15, 30), LocalTime.of(18, 30))
                    )
            );

            seedGuide(userRepository, guideRepository, tourRepository, availabilitySlotRepository, passwordEncoder,
                    "guide.charlottetown@localguide.dev", "Grace", "Murphy",
                    "Prince Edward Island", "Charlottetown", "Waterfront", "English",
                    "Island guide specializing in coastal culture, markets, and Confederation history.",
                    new BigDecimal("4.67"), 12,
                    List.of(
                            new TourSeed("Charlottetown Confederation Tour", "Visit historic sites tied to Confederation and island heritage.", "Historical", "Prince Edward Island", "Charlottetown", "English", new BigDecimal("69.00"), new BigDecimal("2.00"), 10),
                            new TourSeed("PEI Local Tastes Walk", "A tasting-focused route through waterfront and old town favorites.", "Food", "Prince Edward Island", "Charlottetown", "English", new BigDecimal("89.00"), new BigDecimal("2.50"), 8)
                    ),
                    List.of(
                            new SlotSeed(LocalDate.now().plusDays(2), LocalTime.of(11, 0), LocalTime.of(13, 0)),
                            new SlotSeed(LocalDate.now().plusDays(6), LocalTime.of(16, 0), LocalTime.of(18, 30))
                    )
            );

            seedGuide(userRepository, guideRepository, tourRepository, availabilitySlotRepository, passwordEncoder,
                    "guide.kelowna@localguide.dev", "Jack", "Foster",
                    "British Columbia", "Kelowna", "Downtown", "English",
                    "Okanagan guide for vineyard routes, lake views, and relaxed local experiences.",
                    new BigDecimal("4.72"), 13,
                    List.of(
                            new TourSeed("Kelowna Lakeside Highlights", "A scenic tour of lakefront neighborhoods and panoramic stops.", "City", "British Columbia", "Kelowna", "English", new BigDecimal("77.00"), new BigDecimal("2.50"), 9),
                            new TourSeed("Okanagan Taste Trail", "Sample regional flavors with stories from local producers.", "Food", "British Columbia", "Kelowna", "English", new BigDecimal("102.00"), new BigDecimal("3.00"), 7)
                    ),
                    List.of(
                            new SlotSeed(LocalDate.now().plusDays(1), LocalTime.of(9, 30), LocalTime.of(12, 0)),
                            new SlotSeed(LocalDate.now().plusDays(5), LocalTime.of(14, 30), LocalTime.of(17, 30))
                    )
            );

            seedGuide(userRepository, guideRepository, tourRepository, availabilitySlotRepository, passwordEncoder,
                    "guide.surrey@localguide.dev", "Avery", "Sharma",
                    "British Columbia", "Surrey", "City Centre", "English, Hindi",
                    "Metro Vancouver guide with family-friendly city and park routes.",
                    new BigDecimal("4.49"), 7,
                    List.of(
                            new TourSeed("Surrey Urban Green Tour", "A balanced route through parks, plazas, and cultural spaces.", "City", "British Columbia", "Surrey", "English", new BigDecimal("64.00"), new BigDecimal("2.00"), 10),
                            new TourSeed("South Asian Food Streets", "Explore neighborhood food hubs with local culinary insights.", "Food", "British Columbia", "Surrey", "English", new BigDecimal("87.00"), new BigDecimal("2.50"), 8)
                    ),
                    List.of(
                            new SlotSeed(LocalDate.now().plusDays(2), LocalTime.of(12, 0), LocalTime.of(14, 0)),
                            new SlotSeed(LocalDate.now().plusDays(7), LocalTime.of(17, 0), LocalTime.of(19, 30))
                    )
            );

            seedGuide(userRepository, guideRepository, tourRepository, availabilitySlotRepository, passwordEncoder,
                    "guide.hamilton@localguide.dev", "Mason", "Diaz",
                    "Ontario", "Hamilton", "Westdale", "English, Spanish",
                    "Waterfall-city guide offering escarpment views and local neighborhood routes.",
                    new BigDecimal("4.61"), 11,
                    List.of(
                            new TourSeed("Hamilton Waterfall Route", "Visit iconic waterfalls and scenic lookout points near the city.", "Nature", "Ontario", "Hamilton", "English", new BigDecimal("78.00"), new BigDecimal("3.00"), 8),
                            new TourSeed("Hamilton Arts and Industry", "A city route connecting public art and industrial heritage.", "Cultural", "Ontario", "Hamilton", "English", new BigDecimal("83.00"), new BigDecimal("2.50"), 8)
                    ),
                    List.of(
                            new SlotSeed(LocalDate.now().plusDays(3), LocalTime.of(9, 0), LocalTime.of(12, 0)),
                            new SlotSeed(LocalDate.now().plusDays(8), LocalTime.of(14, 0), LocalTime.of(17, 0))
                    )
            );

            seedGuide(userRepository, guideRepository, tourRepository, availabilitySlotRepository, passwordEncoder,
                    "guide.mississauga@localguide.dev", "Chloe", "Ahmed",
                    "Ontario", "Mississauga", "Port Credit", "English, Arabic",
                    "Lakeshore route expert with neighborhood culture and waterfront experiences.",
                    new BigDecimal("4.56"), 9,
                    List.of(
                            new TourSeed("Port Credit Waterfront Walk", "Discover lakeshore stories, marinas, and local landmarks.", "City", "Ontario", "Mississauga", "English", new BigDecimal("67.00"), new BigDecimal("2.00"), 10),
                            new TourSeed("Mississauga Culinary Night", "Evening food route with diverse local highlights.", "Food", "Ontario", "Mississauga", "English", new BigDecimal("91.00"), new BigDecimal("2.50"), 8)
                    ),
                    List.of(
                            new SlotSeed(LocalDate.now().plusDays(1), LocalTime.of(13, 0), LocalTime.of(15, 0)),
                            new SlotSeed(LocalDate.now().plusDays(6), LocalTime.of(18, 0), LocalTime.of(20, 30))
                    )
            );

            seedGuide(userRepository, guideRepository, tourRepository, availabilitySlotRepository, passwordEncoder,
                    "guide.laval@localguide.dev", "Logan", "Boucher",
                    "Quebec", "Laval", "Centropolis", "French, English",
                    "North-shore guide for local culture, parks, and urban family-friendly tours.",
                    new BigDecimal("4.54"), 8,
                    List.of(
                            new TourSeed("Laval Urban Discovery", "Explore local landmarks and modern cultural districts.", "City", "Quebec", "Laval", "French", new BigDecimal("62.00"), new BigDecimal("2.00"), 10),
                            new TourSeed("Laval Parks and Flavors", "A mixed route of green spaces and neighborhood dining spots.", "Nature", "Quebec", "Laval", "French", new BigDecimal("85.00"), new BigDecimal("2.50"), 8)
                    ),
                    List.of(
                            new SlotSeed(LocalDate.now().plusDays(2), LocalTime.of(10, 0), LocalTime.of(12, 0)),
                            new SlotSeed(LocalDate.now().plusDays(7), LocalTime.of(15, 30), LocalTime.of(18, 0))
                    )
            );

            seedGuide(userRepository, guideRepository, tourRepository, availabilitySlotRepository, passwordEncoder,
                    "guide.burnaby@localguide.dev", "Nora", "Evans",
                    "British Columbia", "Burnaby", "Metrotown", "English",
                    "Local guide connecting urban parks, viewpoints, and neighborhood stories.",
                    new BigDecimal("4.50"), 7,
                    List.of(
                            new TourSeed("Burnaby City and Parks", "An easy-paced route across city parks and skyline viewpoints.", "City", "British Columbia", "Burnaby", "English", new BigDecimal("60.00"), new BigDecimal("2.00"), 10),
                            new TourSeed("Burnaby Mountain Sunset", "Evening route featuring panoramic views and local highlights.", "Nature", "British Columbia", "Burnaby", "English", new BigDecimal("88.00"), new BigDecimal("2.50"), 8)
                    ),
                    List.of(
                            new SlotSeed(LocalDate.now().plusDays(3), LocalTime.of(12, 30), LocalTime.of(14, 30)),
                            new SlotSeed(LocalDate.now().plusDays(9), LocalTime.of(17, 30), LocalTime.of(20, 0))
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
