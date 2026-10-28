package com.MNS.global_shipping_platform.auth;

import com.MNS.global_shipping_platform.exception.auth.UserEmailAlreadyExists;
import com.MNS.global_shipping_platform.security.jwt.JwtService;
import com.MNS.global_shipping_platform.user.UserRole;
import com.MNS.global_shipping_platform.user.entity.User;
import com.MNS.global_shipping_platform.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public void register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.email())) {
            throw new UserEmailAlreadyExists(
                    "Email already exists with: " + request.email()
            );
        }

        User user = User.builder()
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .role(UserRole.CUSTOMER)
                .build();

        userRepository.save(user);
    }

    public LoginResponse login(LoginRequest request) {

        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                request.email(),
                                request.password()
                        )
                );

        String role = authentication.getAuthorities()
                .iterator()
                .next()
                .getAuthority()
                .replace("ROLE_", "");

        String token = jwtService.generateToken(
                authentication.getName(),
                role
        );

        return new LoginResponse(token);
    }
}
