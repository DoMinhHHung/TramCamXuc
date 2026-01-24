package iuh.fit.se.tramcamxuc.modules.music.playlist.controller;

import iuh.fit.se.tramcamxuc.modules.music.playlist.dto.request.AddSongRequest;
import iuh.fit.se.tramcamxuc.modules.music.playlist.dto.request.CreatePlaylistRequest;
import iuh.fit.se.tramcamxuc.modules.music.playlist.dto.response.PlaylistResponse;
import iuh.fit.se.tramcamxuc.modules.music.playlist.service.PlaylistService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/playlists")
@RequiredArgsConstructor
public class PlaylistController {

    private final PlaylistService playlistService;

    @PostMapping
    public ResponseEntity<PlaylistResponse> createPlaylist(@RequestBody @Valid CreatePlaylistRequest request) {
        return ResponseEntity.ok(playlistService.createPlaylist(request));
    }

    @GetMapping("/me")
    public ResponseEntity<List<PlaylistResponse>> getMyPlaylists() {
        return ResponseEntity.ok(playlistService.getMyPlaylists());
    }

    @GetMapping("/{slug}")
    public ResponseEntity<PlaylistResponse> getPlaylistBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(playlistService.getPlaylistBySlug(slug));
    }

    @PostMapping("/{id}/songs")
    public ResponseEntity<String> addSong(@PathVariable UUID id, @RequestBody @Valid AddSongRequest request) {
        playlistService.addSongToPlaylist(id, request);
        return ResponseEntity.ok("Đã thêm bài hát vào playlist thành công");
    }

    @DeleteMapping("/{id}/songs/{songId}")
    public ResponseEntity<String> removeSong(@PathVariable UUID id, @PathVariable UUID songId) {
        playlistService.removeSongFromPlaylist(id, songId);
        return ResponseEntity.ok("Đã xóa bài hát khỏi playlist");
    }
}