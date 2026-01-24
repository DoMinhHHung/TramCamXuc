package iuh.fit.se.tramcamxuc.modules.music.song.dto.response;

import iuh.fit.se.tramcamxuc.modules.music.genre.entity.Genre;
import iuh.fit.se.tramcamxuc.modules.music.song.entity.Song;
import iuh.fit.se.tramcamxuc.modules.music.song.entity.enums.SongStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
public class SongResponse {
    private UUID id;
    private String title;
    private String slug;
    private String lyric;
    private String audioUrl;
    private String coverUrl;
    private Integer duration;
    private Long listeningCount;
    private SongStatus status;
    private boolean isVerified;

    private String artistName;
    private String artistSlug;
    private String artistAvatar;

    private Set<String> genres;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static SongResponse fromEntity(Song song) {
        return SongResponse.builder()
                .id(song.getId())
                .title(song.getTitle())
                .slug(song.getSlug())
                .lyric(song.getLyric())
                .audioUrl(song.getAudioUrl())
                .coverUrl(song.getCoverUrl())
                .duration(song.getDuration())
                .listeningCount(song.getListeningCount())
                .status(song.getStatus())
                .isVerified(song.isVerified())
                .artistName(song.getArtist().getName())
                .artistSlug(song.getArtist().getSlug())
                .artistAvatar(song.getArtist().getImageUrl())
                .genres(song.getGenres().stream()
                        .map(Genre::getName)
                        .collect(Collectors.toSet()))
                .createdAt(song.getCreatedAt())
                .updatedAt(song.getUpdatedAt())
                .build();
    }
}