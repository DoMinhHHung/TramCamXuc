package iuh.fit.se.tramcamxuc.modules.music.artist.entity;

import iuh.fit.se.tramcamxuc.modules.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "artist_follows", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "artist_id"})
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class ArtistFollow {

    @EmbeddedId
    private ArtistFollowId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("artistId")
    @JoinColumn(name = "artist_id")
    private Artist artist;

    @CreatedDate
    @Column(name = "followed_at", nullable = false, updatable = false)
    private LocalDateTime followedAt;
}