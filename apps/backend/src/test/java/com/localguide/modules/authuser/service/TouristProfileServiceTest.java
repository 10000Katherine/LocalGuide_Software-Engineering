package com.localguide.modules.authuser.service;

import com.localguide.common.exception.ApiException;
import com.localguide.modules.authuser.domain.User;
import com.localguide.modules.authuser.dto.UpdateTouristProfileRequest;
import com.localguide.modules.authuser.dto.UserResponse;
import com.localguide.modules.authuser.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TouristProfileServiceTest {

    @Mock
    private UserRepository userRepository;

    private TouristProfileService touristProfileService;

    @BeforeEach
    void setUp() {
        touristProfileService = new TouristProfileService(userRepository);
    }

    @Test
    void getByEmailShouldReturnUserResponse() {
        User user = new User();
        user.setEmail("tourist@example.com");
        user.setFirstName("Tour");
        user.setLastName("User");
        user.setPhone("+1-604-555-0101");

        when(userRepository.findByEmail("tourist@example.com")).thenReturn(Optional.of(user));

        UserResponse response = touristProfileService.getByEmail("tourist@example.com");

        assertEquals("tourist@example.com", response.email());
        assertEquals("Tour", response.firstName());
    }

    @Test
    void updateByEmailShouldUpdateFields() {
        User user = new User();
        user.setEmail("tourist@example.com");
        user.setFirstName("Old");
        user.setLastName("Name");

        when(userRepository.findByEmail("tourist@example.com")).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserResponse response = touristProfileService.updateByEmail(
                "tourist@example.com",
                new UpdateTouristProfileRequest("New", "Name", "+1-604-555-0102")
        );

        assertEquals("New", response.firstName());
        assertEquals("Name", response.lastName());
        assertEquals("+1-604-555-0102", response.phone());
    }

    @Test
    void getByEmailShouldThrowNotFoundWhenMissing() {
        when(userRepository.findByEmail("missing@example.com")).thenReturn(Optional.empty());

        ApiException exception = assertThrows(ApiException.class,
                () -> touristProfileService.getByEmail("missing@example.com"));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }
}
