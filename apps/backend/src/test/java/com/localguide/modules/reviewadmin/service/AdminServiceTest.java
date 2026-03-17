package com.localguide.modules.reviewadmin.service;

import com.localguide.common.exception.ApiException;
import com.localguide.modules.authuser.domain.User;
import com.localguide.modules.authuser.domain.UserRole;
import com.localguide.modules.authuser.repository.UserRepository;
import com.localguide.modules.reviewadmin.domain.GuideVerificationRequest;
import com.localguide.modules.reviewadmin.domain.GuideVerificationStatus;
import com.localguide.modules.reviewadmin.dto.AdminAnalyticsResponse;
import com.localguide.modules.reviewadmin.dto.AdminUserResponse;
import com.localguide.modules.reviewadmin.dto.UpdateUserStatusRequest;
import com.localguide.modules.reviewadmin.repository.FavoriteRepository;
import com.localguide.modules.reviewadmin.repository.GuideVerificationRequestRepository;
import com.localguide.modules.reviewadmin.repository.ReviewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private GuideVerificationRequestRepository verificationRepository;

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private FavoriteRepository favoriteRepository;

    private AdminService adminService;

    @BeforeEach
    void setUp() {
        adminService = new AdminService(userRepository, verificationRepository, reviewRepository, favoriteRepository);
    }

    @Test
    void listUsersShouldRequireAdmin() {
        User tourist = createUser(10L, "tourist@example.com", UserRole.TOURIST);
        when(userRepository.findByEmail("tourist@example.com")).thenReturn(Optional.of(tourist));

        ApiException exception = assertThrows(ApiException.class, () -> adminService.listUsers("tourist@example.com"));

        assertEquals(HttpStatus.FORBIDDEN, exception.getStatus());
    }

    @Test
    void updateUserStatusShouldToggleActiveFlag() {
        User admin = createUser(1L, "admin@example.com", UserRole.ADMIN);
        User guide = createUser(2L, "guide@example.com", UserRole.GUIDE);
        guide.setActive(true);

        when(userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(admin));
        when(userRepository.findById(2L)).thenReturn(Optional.of(guide));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(verificationRepository.findByGuideId(2L)).thenReturn(Optional.empty());

        AdminUserResponse response = adminService.updateUserStatus("admin@example.com", 2L, new UpdateUserStatusRequest(false));

        assertEquals(false, response.active());
    }

    @Test
    void analyticsShouldAggregateCounts() {
        User admin = createUser(1L, "admin@example.com", UserRole.ADMIN);
        User guide = createUser(2L, "guide@example.com", UserRole.GUIDE);
        User tourist = createUser(3L, "tourist@example.com", UserRole.TOURIST);

        GuideVerificationRequest verification = new GuideVerificationRequest();
        verification.setGuide(guide);
        verification.setStatus(GuideVerificationStatus.APPROVED);

        when(userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(admin));
        when(userRepository.findAll()).thenReturn(List.of(admin, guide, tourist));
        when(verificationRepository.findAll()).thenReturn(List.of(verification));
        when(verificationRepository.findByStatusOrderByCreatedAtAsc(GuideVerificationStatus.PENDING)).thenReturn(List.of());
        when(reviewRepository.count()).thenReturn(6L);
        when(favoriteRepository.count()).thenReturn(4L);

        AdminAnalyticsResponse response = adminService.analytics("admin@example.com");

        assertEquals(3L, response.totalUsers());
        assertEquals(1L, response.totalGuides());
        assertEquals(1L, response.verifiedGuides());
        assertEquals(6L, response.totalReviews());
        assertEquals(4L, response.totalFavorites());
    }

    private User createUser(Long id, String email, UserRole role) {
        User user = new User();
        try {
            java.lang.reflect.Field field = User.class.getDeclaredField("id");
            field.setAccessible(true);
            field.set(user, id);
        } catch (Exception ignored) {
        }
        user.setEmail(email);
        user.setFirstName("Test");
        user.setLastName("User");
        user.setRole(role);
        user.setActive(true);
        return user;
    }
}
