package com.MNS.global_shipping_platform.security;

import com.MNS.global_shipping_platform.user.UserRole;
import com.MNS.global_shipping_platform.user.entity.User;
import com.MNS.global_shipping_platform.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminSeeder implements CommandLineRunner {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    @Value("${ADMIN_PASSWORD:}")
    private String adminPassword;


    @Override
    public void run(String @NonNull ... args)  {

        if (adminPassword == null || adminPassword.isBlank()){
            System.out.println(
                    "ADMIN_PASSWORD is not set; skipping ADMIN seeding"
            );
            return;
        }

        String adminEmail = "admin@mnsshipping.com";

        if(userRepository.existsByEmail(adminEmail)){
            return;
        }

        User user = User.builder()
                .email(adminEmail)
                .password(passwordEncoder.encode(adminPassword))
                .role(UserRole.ADMIN)
                .build();

        userRepository.save(user);

        System.out.println("ADMIN user created" + adminEmail);
    }
}
