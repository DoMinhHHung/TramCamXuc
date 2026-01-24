package iuh.fit.se.tramcamxuc.modules.music.playlist.repository;

import iuh.fit.se.tramcamxuc.modules.music.playlist.entity.PlaylistSong;
import iuh.fit.se.tramcamxuc.modules.music.playlist.entity.PlaylistSongId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PlaylistSongRepository extends JpaRepository<PlaylistSong, PlaylistSongId> {
    boolean existsByPlaylistIdAndSongId(UUID playlistId, UUID songId);
    void deleteByPlaylistIdAndSongId(UUID playlistId, UUID songId);
}