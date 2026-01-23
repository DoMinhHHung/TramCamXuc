package iuh.fit.se.tramcamxuc.modules.auth.controller;

import iuh.fit.se.tramcamxuc.common.annotation.RateLimit;
import iuh.fit.se.tramcamxuc.modules.auth.dto.request.*;
import iuh.fit.se.tramcamxuc.modules.auth.dto.response.AuthResponse;
import iuh.fit.se.tramcamxuc.modules.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    @RateLimit(key = "register", count = 3, period = 300)
    public ResponseEntity<String> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }
    @PostMapping("/verify")
    public ResponseEntity<String> verify(@RequestBody Map<String, String> request) {
        authService.verifyAccount(request.get("email"), request.get("otp"));
        return ResponseEntity.ok("Account verified successfully");
    }

    @PostMapping("/resend-otp")
    @RateLimit(key = "resend_otp", count = 3, period = 300)
    public ResponseEntity<String> resendOtp(@RequestParam String email) {
        authService.resendOtp(email);
        return ResponseEntity.ok("OTP resent successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@RequestHeader("x-refresh-token") String refreshToken) {
        if (refreshToken == null) {
            throw new RuntimeException("Refresh Token is missing");
        }
        return ResponseEntity.ok(authService.refreshToken(refreshToken));
    }

    @PostMapping("/forgot-password")
    @RateLimit(key = "forgot_password", count = 3, period = 300)
    public ResponseEntity<String> forgotPassword(@RequestBody @Valid ForgotPasswordRequest request) {
        authService.forgotPassword(request.getEmail());
        return ResponseEntity.ok("OTP đã được gửi đến email của bạn.");
    }

    @PostMapping("/reset-password")
    @RateLimit(key = "reset_password", count = 5, period = 300)
    public ResponseEntity<String> resetPassword(@RequestBody @Valid ResetPasswordRequest request) {
        authService.resetPassword(request);
        return ResponseEntity.ok("Đặt lại mật khẩu thành công. Hãy đăng nhập lại.");
    }

    @PostMapping("/resend-forgot-password-otp")
    @RateLimit(key = "resend_forgot_otp", count = 3, period = 300)
    public ResponseEntity<String> resendForgotPasswordOtp(@RequestParam String email) {
        authService.resendForgotPasswordOtp(email);
        return ResponseEntity.ok("OTP mới đã được gửi.");
    }
}
