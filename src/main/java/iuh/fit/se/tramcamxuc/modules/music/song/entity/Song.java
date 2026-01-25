package iuh.fit.se.tramcamxuc.modules.music.song.entity;

import iuh.fit.se.tramcamxuc.common.BaseEntity;
import iuh.fit.se.tramcamxuc.modules.music.album.entity.Album;
import iuh.fit.se.tramcamxuc.modules.music.artist.entity.Artist;
import iuh.fit.se.tramcamxuc.modules.music.genre.entity.Genre;
import iuh.fit.se.tramcamxuc.modules.music.song.entity.enums.SongStatus;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "songs", indexes = {
        @Index(name = "idx_song_slug", columnList = "slug"),
        @Index(name = "idx_song_title", columnList = "title")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Song extends BaseEntity {

    @Column(nullable = false)
    private String title;

    @Column(unique = true, nullable = false)
    private String slug;

    @Column(columnDefinition = "TEXT")
    private String lyric;

    @Column(nullable = false)
    private String audioUrl;

    private String coverUrl;

    @Column(nullable = false)
    private Integer duration;

    @Column(nullable = false)
    private Long listeningCount = 0L;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SongStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "artist_id", nullable = false)
    private Artist artist;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "song_genres",
            joinColumns = @JoinColumn(name = "song_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id")
    )
    @Builder.Default
    private Set<Genre> genres = new HashSet<>();

    @Column(nullable = false)
    private boolean isVerified = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "album_id")
    private Album album;
}