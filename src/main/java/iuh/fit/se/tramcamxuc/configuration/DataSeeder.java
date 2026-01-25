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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.email}")
    private String adminEmail;
    @Value("${app.admin.password}")
    private String adminPassword;

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.findByEmail(adminEmail).isEmpty()) {
            log.info("Chưa có Admin. Đang khởi tạo tài khoản Admin mặc định...");

            User admin = User.builder()
                    .email(adminEmail)
                    .username("admin_tramcamxuc")
                    .fullName("Super Administrator Tram Cam Xuc")
                    .password(passwordEncoder.encode(adminPassword))
                    .role(Role.ADMIN)
                    .isActive(UserStatus.ACTIVE)
                    .provider(AuthProvider.LOCAL)
                    .dob(LocalDate.of(2003, 3, 4))
                    .build();

            userRepository.save(admin);
            log.info(">>> Đã tạo Admin thành công!" + adminEmail);
        } else {
            log.info("Admin đã tồn tại, bỏ qua bước khởi tạo.");
        }
    }
}