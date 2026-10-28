package com.MNS.global_shipping_platform.security;

import com.MNS.global_shipping_platform.user.UserRole;
import com.MNS.global_shipping_platform.user.entity.User;
import com.MNS.global_shipping_platform.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminSeeder implements CommandLineRunner {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String @NonNull ... args) throws Exception {
        String adminEmail = "admin@mnsshiping.com";

        if(userRepository.existsByEmail(adminEmail)){
            return;
        }

        User user = User.builder()
                .email(adminEmail)
                .password(passwordEncoder.encode("admin123"))
                .role(UserRole.ADMIN)
                .build();

        userRepository.save(user);
        System.out.println("Default ADMIN user created: " + adminEmail);
    }
}
