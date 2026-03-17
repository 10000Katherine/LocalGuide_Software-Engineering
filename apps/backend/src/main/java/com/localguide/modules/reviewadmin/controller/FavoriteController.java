package com.localguide.modules.reviewadmin.controller;

import com.localguide.modules.reviewadmin.dto.CreateFavoriteRequest;
import com.localguide.modules.reviewadmin.dto.FavoriteResponse;
import com.localguide.modules.reviewadmin.service.FavoriteService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tourists/me/favorites")
public class FavoriteController {
    private final FavoriteService favoriteService;

    public FavoriteController(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FavoriteResponse create(Authentication authentication, @RequestBody @Valid CreateFavoriteRequest request) {
        return favoriteService.addFavorite(authentication.getName(), request);
    }

    @GetMapping
    public List<FavoriteResponse> list(Authentication authentication) {
        return favoriteService.getFavorites(authentication.getName());
    }

    @DeleteMapping("/{guideId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remove(Authentication authentication, @PathVariable Long guideId) {
        favoriteService.removeFavorite(authentication.getName(), guideId);
    }
}
