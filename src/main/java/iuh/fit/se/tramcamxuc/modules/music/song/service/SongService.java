package iuh.fit.se.tramcamxuc.modules.music.song.service;

import iuh.fit.se.tramcamxuc.modules.music.song.dto.request.*;
import iuh.fit.se.tramcamxuc.modules.music.song.dto.response.SongResponse;
import iuh.fit.se.tramcamxuc.modules.music.song.entity.Song;
import iuh.fit.se.tramcamxuc.modules.music.song.entity.enums.SongStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface SongService {
    SongResponse uploadSong(CreateSongRequest request);
    SongResponse updateSong(UUID songId, UpdateSongRequest request);
    SongResponse changeSongStatus(UUID songId, SongStatus newStatus);

    List<SongResponse> searchSongs(String keyword);
    void incrementListeningCount(UUID songId);

    SongResponse getSongBySlug(String slug);
// Admin
    Page<SongResponse> getPendingSongs(Pageable pageable);
    void approveSong(UUID songId);
    void rejectSong(UUID songId, String reason);
}
