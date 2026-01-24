package iuh.fit.se.tramcamxuc.modules.music.album.service;

import iuh.fit.se.tramcamxuc.modules.music.album.dto.request.*;
import iuh.fit.se.tramcamxuc.modules.music.album.dto.response.AlbumResponse;

import java.util.UUID;

public interface AlbumService {
    AlbumResponse createAlbum(CreateAlbumRequest request);
    void addSongsToAlbum(UUID albumId, AddSongToAlbumRequest request);
    AlbumResponse getAlbumBySlug(String slug);
}
