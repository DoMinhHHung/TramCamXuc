package iuh.fit.se.tramcamxuc.modules.music.artist.dto.response;

import iuh.fit.se.tramcamxuc.modules.music.artist.entity.Artist;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ArtistResponse {
    private String id;
    private String name;
    private String bio;
    private String imageUrl;
    private String slug;
    private boolean isVerified;

    public static ArtistResponse fromEntity(Artist artist) {
        return ArtistResponse.builder()
                .id(artist.getId().toString())
                .name(artist.getName())
                .bio(artist.getBio())
                .imageUrl(artist.getImageUrl())
                .slug(artist.getSlug())
                .isVerified(artist.isVerified())
                .build();
    }
}