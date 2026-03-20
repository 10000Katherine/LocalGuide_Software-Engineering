package com.localguide.modules.guidetour.dto;

import jakarta.validation.constraints.Size;

public record UpdateGuideProfileRequest(
        @Size(max = 100) String province,
        @Size(max = 100) String city,
        @Size(max = 100) String neighborhood,
        @Size(max = 255) String language,
        @Size(max = 2000) String bio,
        @Size(max = 512) String photoUrl
) {
}
