package com.localguide.modules.reviewadmin.dto;

import com.localguide.modules.reviewadmin.domain.Favorite;

import java.time.Instant;

public record FavoriteResponse(
        Long id,
        Long guideId,
        String guideName,
        String guideEmail,
        Instant createdAt
) {
    public static FavoriteResponse from(Favorite favorite) {
        return new FavoriteResponse(
                favorite.getId(),
                favorite.getGuide().getId(),
                favorite.getGuide().getFirstName() + " " + favorite.getGuide().getLastName(),
                favorite.getGuide().getEmail(),
                favorite.getCreatedAt()
        );
    }
}
