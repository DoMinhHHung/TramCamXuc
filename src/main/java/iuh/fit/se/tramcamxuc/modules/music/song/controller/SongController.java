package iuh.fit.se.tramcamxuc.modules.music.song.controller;

import iuh.fit.se.tramcamxuc.modules.music.song.dto.request.*;
import iuh.fit.se.tramcamxuc.modules.music.song.dto.response.SongResponse;
import iuh.fit.se.tramcamxuc.modules.music.song.entity.Song;
import iuh.fit.se.tramcamxuc.modules.music.song.service.impl.SongServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/songs")
@RequiredArgsConstructor
public class SongController {

    private final SongServiceImpl songService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<SongResponse> uploadSong(@ModelAttribute @Valid CreateSongRequest request) {
        return ResponseEntity.ok(songService.uploadSong(request));
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<SongResponse> updateSong(
            @PathVariable UUID id,
            @ModelAttribute @Valid UpdateSongRequest request
    ) {
        return ResponseEntity.ok(songService.updateSong(id, request));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<SongResponse> changeStatus(
            @PathVariable UUID id,
            @RequestBody @Valid ChangeSongStatusRequest request
    ) {
        return ResponseEntity.ok(songService.changeSongStatus(id, request.getStatus()));
    }
}