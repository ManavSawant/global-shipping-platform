package com.MNS.global_shipping_platform.security.jwt;

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
public class OperatorSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    @Override
    public void run(String @NonNull ... args) throws Exception {

        String email = "operator@mnsshiping.com";

        if(userRepository.existsByEmail(email)){
            return;
        }

        User operator = User.builder()
                .email(email)
                .password(passwordEncoder.encode("operator123"))
                .role(UserRole.OPERATOR)
                .build();
        userRepository.save(operator);

        System.out.println("Default OPERATOR user created: " + email);

    }
}
