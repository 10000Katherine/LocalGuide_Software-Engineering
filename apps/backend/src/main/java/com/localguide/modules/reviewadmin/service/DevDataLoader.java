package com.localguide.modules.reviewadmin.service;

import com.localguide.modules.authuser.domain.User;
import com.localguide.modules.authuser.domain.UserRole;
import com.localguide.modules.authuser.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DevDataLoader {
    @Bean
    CommandLineRunner seedDevUsers(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            createIfMissing(userRepository, passwordEncoder, "admin@localguide.dev", "Admin", "User", UserRole.ADMIN);
            createIfMissing(userRepository, passwordEncoder, "guide@localguide.dev", "Guide", "Demo", UserRole.GUIDE);
            createIfMissing(userRepository, passwordEncoder, "tourist@localguide.dev", "Tourist", "Demo", UserRole.TOURIST);
        };
    }

    private void createIfMissing(UserRepository userRepository,
                                 PasswordEncoder passwordEncoder,
                                 String email,
                                 String firstName,
                                 String lastName,
                                 UserRole role) {
        if (userRepository.findByEmail(email).isPresent()) {
            return;
        }
        User user = new User();
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode("Password123!"));
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setPhone("0000000000");
        user.setRole(role);
        user.setActive(true);
        userRepository.save(user);
    }
}
