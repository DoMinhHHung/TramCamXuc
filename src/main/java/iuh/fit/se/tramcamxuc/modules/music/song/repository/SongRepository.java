package iuh.fit.se.tramcamxuc.modules.music.song.repository;

import iuh.fit.se.tramcamxuc.modules.music.song.entity.Song;
import iuh.fit.se.tramcamxuc.modules.music.song.entity.enums.SongStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SongRepository extends JpaRepository<Song, UUID> {
    Optional<Song> findBySlug(String slug);
    boolean existsBySlug(String slug);

    Page<Song> findByStatus(SongStatus status, Pageable pageable);

    @Modifying
    @Transactional
    @Query("UPDATE Song s SET s.listeningCount = s.listeningCount + :count WHERE s.id = :id")
    void incrementListeningCount(UUID id, Long count);
}