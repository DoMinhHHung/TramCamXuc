package iuh.fit.se.tramcamxuc.modules.music.song.service;

import iuh.fit.se.tramcamxuc.modules.music.song.dto.request.*;
import iuh.fit.se.tramcamxuc.modules.music.song.entity.Song;
import iuh.fit.se.tramcamxuc.modules.music.song.entity.enums.SongStatus;

import java.util.UUID;

public interface SongService {
    Song uploadSong(CreateSongRequest request);
    Song updateSong(UUID songId, UpdateSongRequest request);
    Song changeSongStatus(UUID songId, SongStatus newStatus);
}
