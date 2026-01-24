package iuh.fit.se.tramcamxuc.modules.music.album.repository;

import iuh.fit.se.tramcamxuc.modules.music.album.entity.Album;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AlbumRepository extends JpaRepository<Album, UUID> {
    Optional<Album> findBySlug(String slug);
    boolean existsBySlug(String slug);
}