package iuh.fit.se.tramcamxuc.modules.user.service;

import iuh.fit.se.tramcamxuc.modules.user.dto.request.*;
import iuh.fit.se.tramcamxuc.modules.user.dto.response.*;
import iuh.fit.se.tramcamxuc.modules.user.entity.User;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface UserService {
    User getCurrentUser();
    UserProfileResponse getCurrentUserProfile();
    UserProfileResponse updateProfile(UpdateProfileRequest request);
    CompletableFuture<String> uploadAvatar(MultipartFile file);
    UserProfileResponse setFavoriteGenres(OnboardingGenreRequest request);
    void requestChangePasswordOtp();
    void changePasswordWithOtp(ChangePasswordRequest request);

    PublicProfileResponse getPublicProfile(UUID userId);

}
