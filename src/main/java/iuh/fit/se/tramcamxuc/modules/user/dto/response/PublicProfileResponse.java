package iuh.fit.se.tramcamxuc.modules.user.dto.response;

import iuh.fit.se.tramcamxuc.modules.music.playlist.dto.response.PlaylistResponse;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class PublicProfileResponse {
    private String id;
    private String name;
    private String avatar;
    private String bio;
    private boolean isVerified;
    private int followerCount;
    private List<PlaylistResponse> publicPlaylists;
}