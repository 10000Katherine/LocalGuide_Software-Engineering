package com.localguide.modules.reviewadmin.service;

import com.localguide.common.exception.ApiException;
import com.localguide.modules.authuser.domain.User;
import com.localguide.modules.authuser.domain.UserRole;
import com.localguide.modules.authuser.repository.UserRepository;
import com.localguide.modules.reviewadmin.domain.GuideVerificationStatus;
import com.localguide.modules.reviewadmin.dto.AdminAnalyticsResponse;
import com.localguide.modules.reviewadmin.dto.AdminUserResponse;
import com.localguide.modules.reviewadmin.dto.UpdateUserStatusRequest;
import com.localguide.modules.reviewadmin.repository.FavoriteRepository;
import com.localguide.modules.reviewadmin.repository.GuideVerificationRequestRepository;
import com.localguide.modules.reviewadmin.repository.ReviewRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminService {
    private final UserRepository userRepository;
    private final GuideVerificationRequestRepository verificationRepository;
    private final ReviewRepository reviewRepository;
    private final FavoriteRepository favoriteRepository;

    public AdminService(UserRepository userRepository,
                        GuideVerificationRequestRepository verificationRepository,
                        ReviewRepository reviewRepository,
                        FavoriteRepository favoriteRepository) {
        this.userRepository = userRepository;
        this.verificationRepository = verificationRepository;
        this.reviewRepository = reviewRepository;
        this.favoriteRepository = favoriteRepository;
    }

    public List<AdminUserResponse> listUsers(String adminEmail) {
        requireAdmin(adminEmail);
        return userRepository.findAll().stream()
                .map(user -> AdminUserResponse.from(
                        user,
                        verificationRepository.findByGuideId(user.getId()).map(v -> v.getStatus()).orElse(GuideVerificationStatus.NOT_SUBMITTED)
                ))
                .toList();
    }

    public AdminUserResponse updateUserStatus(String adminEmail, Long userId, UpdateUserStatusRequest request) {
        requireAdmin(adminEmail);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found"));
        user.setActive(request.active());
        userRepository.save(user);
        GuideVerificationStatus verificationStatus = verificationRepository.findByGuideId(user.getId())
                .map(v -> v.getStatus())
                .orElse(GuideVerificationStatus.NOT_SUBMITTED);
        return AdminUserResponse.from(user, verificationStatus);
    }

    public AdminAnalyticsResponse analytics(String adminEmail) {
        requireAdmin(adminEmail);
        List<User> users = userRepository.findAll();
        long totalUsers = users.size();
        long activeUsers = users.stream().filter(User::isActive).count();
        long totalGuides = users.stream().filter(user -> user.getRole() == UserRole.GUIDE).count();
        long totalTourists = users.stream().filter(user -> user.getRole() == UserRole.TOURIST).count();
        long verifiedGuides = verificationRepository.findAll().stream().filter(v -> v.getStatus() == GuideVerificationStatus.APPROVED).count();
        long pendingVerifications = verificationRepository.findByStatusOrderByCreatedAtAsc(GuideVerificationStatus.PENDING).size();
        long totalReviews = reviewRepository.count();
        long totalFavorites = favoriteRepository.count();
        return new AdminAnalyticsResponse(totalUsers, activeUsers, totalGuides, totalTourists, verifiedGuides, pendingVerifications, totalReviews, totalFavorites);
    }

    private void requireAdmin(String email) {
        User admin = userRepository.findByEmail(email)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found"));
        if (admin.getRole() != UserRole.ADMIN) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Only admins can perform this action");
        }
    }
}
