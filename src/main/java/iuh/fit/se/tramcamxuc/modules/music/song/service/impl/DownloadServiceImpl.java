package iuh.fit.se.tramcamxuc.modules.music.song.service.impl;

import iuh.fit.se.tramcamxuc.common.exception.AppException;
import iuh.fit.se.tramcamxuc.common.exception.ResourceNotFoundException;
import iuh.fit.se.tramcamxuc.common.service.MinioService;
import iuh.fit.se.tramcamxuc.modules.music.song.entity.Song;
import iuh.fit.se.tramcamxuc.modules.music.song.repository.SongRepository;
import iuh.fit.se.tramcamxuc.modules.music.song.service.DownloadService;
import iuh.fit.se.tramcamxuc.modules.subscription.repository.UserSubscriptionRepository;
import iuh.fit.se.tramcamxuc.modules.user.entity.User;
import iuh.fit.se.tramcamxuc.modules.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DownloadServiceImpl implements DownloadService {

    private final SongRepository songRepository;
    private final UserSubscriptionRepository userSubRepo;
    private final UserService userService;
    private final MinioService minioService;

    @Override
    public String generateDownloadLink(UUID songId) {
        User user = userService.getCurrentUser();

        var activeSub = userSubRepo.findActiveSubscriptionByUserId(user.getId())
                .orElseThrow(() -> new AppException("You need an active subscription to download songs."));

        Map<String, Object> features = activeSub.getPlan().getFeatures();
        boolean canDownload = (boolean) features.getOrDefault("can_download", false);

        if (!canDownload) {
            throw new AppException("Your current plan does not support downloading songs. Please upgrade!");
        }

        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new ResourceNotFoundException("Song not found"));

        return minioService.getPresignedDownloadUrl(song.getAudioUrl(), 300);
    }
}