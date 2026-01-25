package iuh.fit.se.tramcamxuc.modules.music.artist.repository;

import iuh.fit.se.tramcamxuc.modules.music.artist.entity.ArtistFollow;
import iuh.fit.se.tramcamxuc.modules.music.artist.entity.ArtistFollowId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ArtistFollowRepository extends JpaRepository<ArtistFollow, ArtistFollowId> {
    long countByArtistId(UUID artistId);

    boolean existsByUserIdAndArtistId(UUID userId, UUID artistId);

    List<ArtistFollow> findByUserId(UUID userId);
}