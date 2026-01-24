package iuh.fit.se.tramcamxuc.modules.music.artist.repository;

import iuh.fit.se.tramcamxuc.modules.music.artist.entity.Artist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ArtistRepository extends JpaRepository<Artist, UUID> {
    Optional<Artist> findByUserId(UUID userId);
    boolean existsByName(String name);
}