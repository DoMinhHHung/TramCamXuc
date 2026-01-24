package iuh.fit.se.tramcamxuc.modules.music.album.dto.response;

import iuh.fit.se.tramcamxuc.modules.music.album.entity.Album;
import iuh.fit.se.tramcamxuc.modules.music.song.dto.response.SongResponse;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Getter @Setter @Builder
public class AlbumResponse {
    private String id;
    private String title;
    private String slug;
    private String description;
    private String coverUrl;
    private LocalDate releaseDate;
    private Integer totalDuration;

    private String artistName;
    private String artistSlug;

    private List<SongResponse> songs;

    public static AlbumResponse fromEntity(Album album) {
        return AlbumResponse.builder()
                .id(album.getId().toString())
                .title(album.getTitle())
                .slug(album.getSlug())
                .description(album.getDescription())
                .coverUrl(album.getCoverUrl())
                .releaseDate(album.getReleaseDate())
                .totalDuration(album.getTotalDuration())
                .artistName(album.getArtist().getName())
                .artistSlug(album.getArtist().getSlug())
                .songs(album.getSongs().stream()
                        .map(SongResponse::fromEntity)
                        .collect(Collectors.toList()))
                .build();
    }
}