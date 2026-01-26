package iuh.fit.se.tramcamxuc.modules.music.song.repository;

import iuh.fit.se.tramcamxuc.modules.music.song.entity.Song;
import iuh.fit.se.tramcamxuc.modules.music.song.entity.enums.SongStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SongRepository extends JpaRepository<Song, UUID> {
    @EntityGraph(attributePaths = {"artist", "genres", "album"})
    Optional<Song> findBySlug(String slug);
    boolean existsBySlug(String slug);

    @EntityGraph(attributePaths = {"artist", "genres", "album"})
    Page<Song> findByStatus(SongStatus status, Pageable pageable);

    @Modifying
    @Transactional
    @Query("UPDATE Song s SET s.listeningCount = s.listeningCount + :count WHERE s.id = :id")
    void incrementListeningCount(UUID id, Long count);

    @Query(value = """
                SELECT s.* FROM songs s
                LEFT JOIN artists a ON s.artist_id = a.id
                WHERE s.title ILIKE concat('%', :keyword, '%')
                OR a.name ILIKE concat('%', :keyword, '%')
                ORDER BY
                  CASE WHEN s.title ILIKE :keyword THEN 1 
                       WHEN s.title ILIKE concat(:keyword, '%') THEN 2 
                       ELSE 3 END,
                  s.listening_count DESC
            """, nativeQuery = true)
    List<Song> searchByKeyword(@Param("keyword") String keyword);

    @Modifying
    @Transactional
    @Query("UPDATE Song s SET s.likeCount = :count WHERE s.id = :id")
    void updateLikeCount(UUID id, Long count);

    @EntityGraph(attributePaths = {"artist", "genres"})
    Page<Song> findByArtistIdAndStatus(UUID artistId, SongStatus status, Pageable pageable);
}