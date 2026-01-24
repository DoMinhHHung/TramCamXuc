package iuh.fit.se.tramcamxuc.modules.user.controller;

import iuh.fit.se.tramcamxuc.common.annotation.RateLimit;
import iuh.fit.se.tramcamxuc.modules.user.dto.request.*;
import iuh.fit.se.tramcamxuc.modules.user.dto.response.UserProfileResponse;
import iuh.fit.se.tramcamxuc.modules.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> getMe() {
        return ResponseEntity.ok(userService.getCurrentUserProfile());
    }

    @PutMapping("/profile")
    public ResponseEntity<UserProfileResponse> updateProfile(@RequestBody UpdateProfileRequest request) {
        return ResponseEntity.ok(userService.updateProfile(request));
    }

    @PostMapping("/change-password/otp")
    @RateLimit(key = "req_change_pass_otp", count = 3, period = 300)
    public ResponseEntity<String> requestChangePasswordOtp() {
        userService.requestChangePasswordOtp();
        return ResponseEntity.ok("OTP đã được gửi đến email của bạn.");
    }

    @PostMapping(value = "/avatar", consumes = "multipart/form-data")
    public CompletableFuture<ResponseEntity<String>> uploadAvatar(@RequestParam("file") MultipartFile file) {
        return userService.uploadAvatar(file)
                .thenApply(ResponseEntity::ok)
                .exceptionally(ex -> ResponseEntity.badRequest().body("Lỗi upload: " + ex.getMessage()));
    }

    @PostMapping("/onboarding/genres")
    public ResponseEntity<UserProfileResponse> setFavoriteGenres(@RequestBody @Valid OnboardingGenreRequest request) {
        return ResponseEntity.ok(userService.setFavoriteGenres(request));
    }
}