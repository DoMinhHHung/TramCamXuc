package iuh.fit.se.tramcamxuc.modules.music.playlist.repository;

import iuh.fit.se.tramcamxuc.modules.music.playlist.entity.Playlist;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PlaylistRepository extends JpaRepository<Playlist, UUID> {
    @EntityGraph(attributePaths = {"playlistSongs", "playlistSongs.song", "playlistSongs.song.artist"})
    Optional<Playlist> findBySlug(String slug);
    List<Playlist> findByUserId(UUID userId);
    boolean existsBySlug(String slug);
}