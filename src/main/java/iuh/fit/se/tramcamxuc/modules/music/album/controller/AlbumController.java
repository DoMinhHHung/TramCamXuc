package iuh.fit.se.tramcamxuc.modules.music.album.controller;

import iuh.fit.se.tramcamxuc.modules.music.album.dto.request.AddSongToAlbumRequest;
import iuh.fit.se.tramcamxuc.modules.music.album.dto.request.CreateAlbumRequest;
import iuh.fit.se.tramcamxuc.modules.music.album.dto.response.AlbumResponse;
import iuh.fit.se.tramcamxuc.modules.music.album.service.impl.AlbumServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/albums")
@RequiredArgsConstructor
public class AlbumController {

    private final AlbumServiceImpl albumService;

    @GetMapping("/{slug}")
    public ResponseEntity<AlbumResponse> getAlbum(@PathVariable String slug) {
        return ResponseEntity.ok(albumService.getAlbumBySlug(slug));
    }

    @PostMapping
    public ResponseEntity<AlbumResponse> createAlbum(@RequestBody @Valid CreateAlbumRequest request) {
        return ResponseEntity.ok(albumService.createAlbum(request));
    }

    @PostMapping("/{id}/songs")
    public ResponseEntity<String> addSongs(@PathVariable UUID id, @RequestBody @Valid AddSongToAlbumRequest request) {
        albumService.addSongsToAlbum(id, request);
        return ResponseEntity.ok("Đã thêm bài hát vào album thành công.");
    }
}