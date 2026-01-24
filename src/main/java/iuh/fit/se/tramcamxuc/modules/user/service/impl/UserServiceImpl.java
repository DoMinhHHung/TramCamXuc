package iuh.fit.se.tramcamxuc.modules.user.service.impl;

import iuh.fit.se.tramcamxuc.common.exception.AppException;
import iuh.fit.se.tramcamxuc.common.exception.ResourceNotFoundException;
import iuh.fit.se.tramcamxuc.common.service.CloudinaryService;
import iuh.fit.se.tramcamxuc.common.service.EmailService;
import iuh.fit.se.tramcamxuc.modules.music.entity.Genre;
import iuh.fit.se.tramcamxuc.modules.music.repository.GenreRepository;
import iuh.fit.se.tramcamxuc.modules.user.dto.request.ChangePasswordRequest;
import iuh.fit.se.tramcamxuc.modules.user.dto.request.OnboardingGenreRequest;
import iuh.fit.se.tramcamxuc.modules.user.dto.request.UpdateProfileRequest;
import iuh.fit.se.tramcamxuc.modules.user.dto.response.UserProfileResponse;
import iuh.fit.se.tramcamxuc.modules.user.entity.User;
import iuh.fit.se.tramcamxuc.modules.user.repository.UserRepository;
import iuh.fit.se.tramcamxuc.modules.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.security.SecureRandom;
import java.time.Duration;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final GenreRepository genreRepository;
    private final CloudinaryService cloudinaryService;
    private final PasswordEncoder passwordEncoder;
    private final StringRedisTemplate redisTemplate;
    private final EmailService emailService;
    private static final SecureRandom secureRandom = new SecureRandom();

    @Override
    public User getCurrentUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new AppException("User not authenticated");
        }
        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }

    @Override
    public UserProfileResponse getCurrentUserProfile() {
        return UserProfileResponse.fromEntity(getCurrentUser());
    }

    @Override
    public UserProfileResponse updateProfile(UpdateProfileRequest request) {
        User user = getCurrentUser();

        if (request.getFullName() != null)
            user.setFullName(request.getFullName());
        if (request.getDob() != null)
            user.setDob(request.getDob());
        if (request.getGender() != null)
            user.setGender(request.getGender());

        return UserProfileResponse.fromEntity(userRepository.save(user));
    }

    @Override
    @Transactional
    public CompletableFuture<String> uploadAvatar(MultipartFile file) {
        User user = getCurrentUser();

        return cloudinaryService.uploadAvatarAsync(file, user.getId().toString())
                .thenApply(newAvatarUrl -> {
                    user.setAvatarUrl(newAvatarUrl);
                    userRepository.save(user);
                    return newAvatarUrl;
                });
    }

    @Transactional
    public UserProfileResponse setFavoriteGenres(OnboardingGenreRequest request) {
        User user = getCurrentUser();

        List<Genre> genres = genreRepository.findAllById(request.getGenreIds());

        if (genres.isEmpty() || genres.size() > 5) {
            throw new AppException("Genres must be between 1 and 5");
        }

        user.setFavoriteGenres(new HashSet<>(genres));
        return UserProfileResponse.fromEntity(userRepository.save(user));
    }

    @Override
    public void requestChangePasswordOtp() {
        User user = getCurrentUser();

        String otp = String.valueOf(secureRandom.nextInt(900000) + 100000);
        String key = "CHANGE_PASS_OTP:" + user.getEmail();
        redisTemplate.opsForValue().set(key, otp, Duration.ofMinutes(5));

        emailService.sendHtmlEmail(
                user.getEmail(),
                "OTP Đổi mật khẩu Phazel Sound",
                "email/change-password-otp",
                Map.of("name", user.getFullName(), "otp", otp)
        );
    }

    @Override
    @Transactional
    public void changePasswordWithOtp(ChangePasswordRequest request) {
        User user = getCurrentUser();

        String key = "CHANGE_PASS_OTP:" + user.getEmail();
        String storedOtp = redisTemplate.opsForValue().get(key);

        if (storedOtp == null || !storedOtp.equals(request.getOtp())) {
            throw new AppException("OTP is invalid or has expired");
        }

        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new AppException("Confirm password does not match");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        redisTemplate.delete(key);
    }
}
