package iuh.fit.se.tramcamxuc.modules.music.artist.service;

import iuh.fit.se.tramcamxuc.modules.music.artist.dto.request.CreateArtistRequest;
import iuh.fit.se.tramcamxuc.modules.music.artist.entity.Artist;

public interface ArtistService {
    Artist createSystemArtist(CreateArtistRequest request);
    Artist registerAsArtist(CreateArtistRequest request);

}
