package iuh.fit.se.tramcamxuc.modules.music.song.repository;

import iuh.fit.se.tramcamxuc.modules.music.song.entity.Song;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SongRepository extends JpaRepository<Song, UUID> {
    Optional<Song> findBySlug(String slug);
    boolean existsBySlug(String slug);
}