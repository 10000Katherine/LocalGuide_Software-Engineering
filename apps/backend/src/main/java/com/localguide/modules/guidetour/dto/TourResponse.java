package com.localguide.modules.guidetour.dto;

import com.localguide.modules.authuser.domain.User;
import com.localguide.modules.guidetour.domain.Guide;
import com.localguide.modules.guidetour.domain.Tour;

import java.math.BigDecimal;

public record TourResponse(
        Long id,
        Long guideId,
        String guideName,
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
    public static TourResponse from(Tour tour) {
        Guide guide = tour.getGuide();
        User user = guide != null ? guide.getUser() : null;
        String firstName = user != null && user.getFirstName() != null ? user.getFirstName() : "Guide";
        String lastName = user != null && user.getLastName() != null ? user.getLastName() : "";
        String guideName = (firstName + " " + lastName).trim();
        Long guideId = guide != null ? guide.getId() : null;

        return new TourResponse(
                tour.getId(),
                guideId,
                guideName,
                tour.getTitle(),
                tour.getDescription(),
                tour.getCategory(),
                tour.getProvince(),
                tour.getCity(),
                tour.getLanguage(),
                tour.getPrice(),
                tour.getDurationHours(),
                tour.getGroupSize()
        );
    }
}
