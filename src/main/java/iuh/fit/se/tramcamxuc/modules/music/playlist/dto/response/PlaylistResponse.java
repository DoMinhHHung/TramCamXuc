package iuh.fit.se.tramcamxuc.modules.music.playlist.dto.response;

import iuh.fit.se.tramcamxuc.modules.music.playlist.entity.Playlist;
import iuh.fit.se.tramcamxuc.modules.music.playlist.entity.PlaylistSong;
import iuh.fit.se.tramcamxuc.modules.music.song.dto.response.SongResponse;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Getter @Setter @Builder
public class PlaylistResponse {
    private String id;
    private String title;
    private String slug;
    private String description;
    private String coverUrl;
    private boolean isPublic;
    private String ownerName;
    private List<SongResponse> songs;

    public static PlaylistResponse fromEntity(Playlist playlist) {
        return PlaylistResponse.builder()
                .id(playlist.getId().toString())
                .title(playlist.getTitle())
                .slug(playlist.getSlug())
                .coverUrl(playlist.getCoverUrl())
                .isPublic(playlist.isPublic())
                .ownerName(playlist.getUser().getFullName())
                .songs(playlist.getPlaylistSongs().stream()
                        .sorted(Comparator.comparing(PlaylistSong::getAddedAt, Comparator.reverseOrder()))
                        .map(ps -> SongResponse.fromEntity(ps.getSong()))
                        .collect(Collectors.toList()))
                .build();
    }
}