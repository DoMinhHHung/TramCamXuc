package iuh.fit.se.tramcamxuc.modules.auth.service;

import iuh.fit.se.tramcamxuc.modules.auth.dto.request.RegisterRequest;
import iuh.fit.se.tramcamxuc.modules.user.entity.User;

public interface AuthService {
    String register(RegisterRequest request);
    void verifyAccount(String email, String otp);
    void resendOtp(String email);

}
