package iuh.fit.se.tramcamxuc.modules.music.artist.service;

import iuh.fit.se.tramcamxuc.modules.music.artist.dto.request.CreateArtistRequest;
import iuh.fit.se.tramcamxuc.modules.music.artist.dto.response.ArtistResponse;
import iuh.fit.se.tramcamxuc.modules.music.artist.entity.Artist;

import java.util.UUID;

public interface ArtistService {
    ArtistResponse createSystemArtist(CreateArtistRequest request);

    ArtistResponse registerAsArtist(CreateArtistRequest request);

    void followArtist(UUID artistId);

    void unfollowArtist(UUID artistId);

}
