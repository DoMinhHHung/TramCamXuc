package iuh.fit.se.tramcamxuc.modules.music.playlist.service;

import iuh.fit.se.tramcamxuc.modules.music.playlist.dto.request.AddSongRequest;
import iuh.fit.se.tramcamxuc.modules.music.playlist.dto.request.CreatePlaylistRequest;
import iuh.fit.se.tramcamxuc.modules.music.playlist.dto.response.PlaylistResponse;
import java.util.List;
import java.util.UUID;

public interface PlaylistService {
    PlaylistResponse createPlaylist(CreatePlaylistRequest request);
    void addSongToPlaylist(UUID playlistId, AddSongRequest request);
    void removeSongFromPlaylist(UUID playlistId, UUID songId);
    PlaylistResponse getPlaylistBySlug(String slug);
    List<PlaylistResponse> getMyPlaylists();
}