package iuh.fit.se.tramcamxuc.modules.music.genre.repository;

import iuh.fit.se.tramcamxuc.modules.admin.dto.ChartData;
import iuh.fit.se.tramcamxuc.modules.admin.dto.projection.ChartDataProjection;
import iuh.fit.se.tramcamxuc.modules.music.genre.entity.Genre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface GenreRepository extends JpaRepository<Genre, UUID> {
    Optional<Genre> findByName(String name);

    boolean existsByName(String name);

    @Query("SELECT COUNT(s) > 0 FROM Song s JOIN s.genres g WHERE g.id = :genreId")
    boolean hasSongs(UUID genreId);

    @Query("SELECT g.name as label, COUNT(DISTINCT s.id) as value " +
            "FROM Genre g LEFT JOIN Song s ON g MEMBER OF s.genres " +
            "GROUP BY g.id, g.name")
    List<ChartDataProjection> getSongCountByGenre();
}
