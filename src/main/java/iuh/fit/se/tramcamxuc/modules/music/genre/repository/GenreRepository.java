package iuh.fit.se.tramcamxuc.modules.music.genre.repository;

import iuh.fit.se.tramcamxuc.modules.admin.dto.ChartData;
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

    @Query("SELECT new iuh.fit.se.tramcamxuc.modules.admin.dto.ChartData(g.name, COUNT(s)) " +
            "FROM Song s " +
            "JOIN s.genres g " +
            "GROUP BY g.id, g.name")
    List<ChartData> getGenreSongCount();
}
