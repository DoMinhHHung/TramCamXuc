package iuh.fit.se.tramcamxuc.configuration;

import iuh.fit.se.tramcamxuc.modules.user.entity.User;
import iuh.fit.se.tramcamxuc.modules.user.entity.enums.AuthProvider;
import iuh.fit.se.tramcamxuc.modules.user.entity.enums.Role;
import iuh.fit.se.tramcamxuc.modules.user.entity.enums.UserStatus;
import iuh.fit.se.tramcamxuc.modules.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DatabaseInitializer {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.email}")
    private String adminEmail;
    @Value("${app.admin.password}")
    private String adminPassword;

    @Bean
    public CommandLineRunner initDatabase() {
        return args -> {
            if (userRepository.findByEmail(adminEmail).isEmpty()) {
                log.info("Creating default ADMIN account...");

                User admin = User.builder()
                        .email(adminEmail)
                        .username("admin_tramcamxuc")
                        .password(passwordEncoder.encode(adminPassword))
                        .fullName("Super Administrator Tram Cam Xuc")
                        .role(Role.ADMIN)
                        .provider(AuthProvider.LOCAL)
                        .isActive(UserStatus.ACTIVE)
                        .dob(LocalDate.of(2003, 3, 4))
                        .build();

                userRepository.save(admin);
                log.info("Admin account created: email={}", adminEmail);
            }
        };
    }
}