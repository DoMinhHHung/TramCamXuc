package iuh.fit.se.tramcamxuc.modules.auth.service.impl;

import iuh.fit.se.tramcamxuc.modules.auth.dto.request.RegisterRequest;
import iuh.fit.se.tramcamxuc.modules.auth.service.AuthService;
import iuh.fit.se.tramcamxuc.modules.user.entity.User;
import iuh.fit.se.tramcamxuc.modules.user.entity.enums.*;
import iuh.fit.se.tramcamxuc.modules.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public String register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already in use");
        }
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already in use");
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .role(Role.USER)
                .provider(AuthProvider.LOCAL)
                .isActive(UserStatus.UNVERIFIED)
                .build();

        userRepository.save(user);

        return "User registered successfully";
    }
}
