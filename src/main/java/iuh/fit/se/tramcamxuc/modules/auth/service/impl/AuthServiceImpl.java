package iuh.fit.se.tramcamxuc.modules.auth.service.impl;

import iuh.fit.se.tramcamxuc.common.service.EmailService;
import iuh.fit.se.tramcamxuc.modules.auth.dto.request.RegisterRequest;
import iuh.fit.se.tramcamxuc.modules.auth.dto.request.VerifyAccountRequest;
import iuh.fit.se.tramcamxuc.modules.auth.service.AuthService;
import iuh.fit.se.tramcamxuc.modules.user.entity.User;
import iuh.fit.se.tramcamxuc.modules.user.entity.enums.AuthProvider;
import iuh.fit.se.tramcamxuc.modules.user.entity.enums.Role;
import iuh.fit.se.tramcamxuc.modules.user.entity.enums.UserStatus;
import iuh.fit.se.tramcamxuc.modules.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.Map;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final StringRedisTemplate redisTemplate;

    @Value("${application.security.otp.expiration-minutes}")
    private long otpExpirationMinutes;

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

        sendVerificationOtp(user);

        return "Registration successful. Please check your email for OTP.";
    }

    public void verifyAccount(String email, String otp) {
        String key = "OTP:" + email;
        String storedOtp = redisTemplate.opsForValue().get(key);

        if (storedOtp == null || !storedOtp.equals(otp)) {
            throw new RuntimeException("Invalid or expired OTP");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getIsActive() == UserStatus.ACTIVE) {
            throw new RuntimeException("Account already verified");
        }

        user.setIsActive(UserStatus.ACTIVE);
        userRepository.save(user);

        redisTemplate.delete(key);
    }

    public void resendOtp(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getIsActive() == UserStatus.ACTIVE) {
            throw new RuntimeException("Account already verified");
        }

        sendVerificationOtp(user);
    }

    private void sendVerificationOtp(User user) {
        String otp = String.valueOf(new Random().nextInt(900000) + 100000); // 6 digits

        redisTemplate.opsForValue().set(
                "OTP:" + user.getEmail(),
                otp,
                Duration.ofMinutes(otpExpirationMinutes)
        );

        emailService.sendHtmlEmail(
                user.getEmail(),
                "Xác thực tài khoản Phazel Sound",
                "email/register-otp",
                Map.of("name", user.getFullName(), "otp", otp)
        );
    }
}