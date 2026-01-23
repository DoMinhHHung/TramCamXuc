package iuh.fit.se.tramcamxuc.modules.auth.controller;

import iuh.fit.se.tramcamxuc.common.annotation.RateLimit;
import iuh.fit.se.tramcamxuc.modules.auth.dto.request.*;
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
}
