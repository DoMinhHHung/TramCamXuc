package iuh.fit.se.tramcamxuc.modules.auth.dto.response;

import lombok.Builder;

@Builder
public class AuthResponse {
    private String message;
    private String accessToken;
    private String refreshToken;
}
