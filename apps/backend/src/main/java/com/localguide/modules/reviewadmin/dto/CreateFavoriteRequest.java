package com.localguide.modules.reviewadmin.dto;

import jakarta.validation.constraints.NotNull;

public record CreateFavoriteRequest(@NotNull Long guideId) {
}
