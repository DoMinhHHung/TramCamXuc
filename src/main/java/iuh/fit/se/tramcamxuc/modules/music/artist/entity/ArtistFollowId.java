package iuh.fit.se.tramcamxuc.modules.music.artist.entity;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.util.UUID;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ArtistFollowId {
    private UUID userId;
    private UUID artistId;
}
