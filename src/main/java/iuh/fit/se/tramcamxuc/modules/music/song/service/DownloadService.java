package iuh.fit.se.tramcamxuc.modules.music.song.service;

import java.util.UUID;

public interface DownloadService {
    String generateDownloadLink(UUID songId);
}