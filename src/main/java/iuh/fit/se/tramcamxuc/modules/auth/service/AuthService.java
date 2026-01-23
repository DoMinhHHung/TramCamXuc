package iuh.fit.se.tramcamxuc.modules.auth.service;

import iuh.fit.se.tramcamxuc.modules.auth.dto.request.RegisterRequest;

public interface AuthService {
    String register(RegisterRequest request);

}
