package com.localguide.modules.reviewadmin.service;

import com.localguide.common.exception.ApiException;
import com.localguide.modules.authuser.domain.User;
import com.localguide.modules.authuser.domain.UserRole;
import com.localguide.modules.authuser.repository.UserRepository;
import com.localguide.modules.reviewadmin.domain.Favorite;
import com.localguide.modules.reviewadmin.dto.CreateFavoriteRequest;
import com.localguide.modules.reviewadmin.dto.FavoriteResponse;
import com.localguide.modules.reviewadmin.repository.FavoriteRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FavoriteService {
    private final FavoriteRepository favoriteRepository;
    private final UserRepository userRepository;

    public FavoriteService(FavoriteRepository favoriteRepository, UserRepository userRepository) {
        this.favoriteRepository = favoriteRepository;
        this.userRepository = userRepository;
    }

    public FavoriteResponse addFavorite(String touristEmail, CreateFavoriteRequest request) {
        User tourist = getRequiredUserByEmail(touristEmail);
        requireRole(tourist, UserRole.TOURIST, "Only tourists can add favorites");

        User guide = userRepository.findById(request.guideId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Guide not found"));
        requireRole(guide, UserRole.GUIDE, "Selected user is not a guide");

        favoriteRepository.findByTouristIdAndGuideId(tourist.getId(), guide.getId()).ifPresent(existing -> {
            throw new ApiException(HttpStatus.CONFLICT, "Guide is already in favorites");
        });

        Favorite favorite = new Favorite();
        favorite.setTourist(tourist);
        favorite.setGuide(guide);
        return FavoriteResponse.from(favoriteRepository.save(favorite));
    }

    public List<FavoriteResponse> getFavorites(String touristEmail) {
        User tourist = getRequiredUserByEmail(touristEmail);
        requireRole(tourist, UserRole.TOURIST, "Only tourists can view favorites");

        return favoriteRepository.findByTouristIdOrderByCreatedAtDesc(tourist.getId())
                .stream()
                .map(FavoriteResponse::from)
                .toList();
    }

    public void removeFavorite(String touristEmail, Long guideId) {
        User tourist = getRequiredUserByEmail(touristEmail);
        requireRole(tourist, UserRole.TOURIST, "Only tourists can remove favorites");

        Favorite favorite = favoriteRepository.findByTouristIdAndGuideId(tourist.getId(), guideId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Favorite not found"));
        favoriteRepository.delete(favorite);
    }

    private User getRequiredUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found"));
    }

    private void requireRole(User user, UserRole expected, String message) {
        if (user.getRole() != expected) {
            throw new ApiException(HttpStatus.FORBIDDEN, message);
        }
    }
}
