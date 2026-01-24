package iuh.fit.se.tramcamxuc.modules.music.playlist.entity;

import jakarta.persistence.Embeddable;
import lombok.*;
import java.io.Serializable;
import java.util.UUID;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class PlaylistSongId implements Serializable {
    private UUID playlistId;
    private UUID songId;
}