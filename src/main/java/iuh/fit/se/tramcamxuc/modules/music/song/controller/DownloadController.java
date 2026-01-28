package iuh.fit.se.tramcamxuc.modules.music.song.controller;

import iuh.fit.se.tramcamxuc.common.exception.dto.ApiResponse;
import iuh.fit.se.tramcamxuc.modules.music.song.service.DownloadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/downloads")
@RequiredArgsConstructor
public class DownloadController {

    private final DownloadService downloadService;

    @GetMapping("/song/{songId}")
    public ResponseEntity<?> downloadSong(@PathVariable UUID songId) {
        String url = downloadService.generateDownloadLink(songId);
        return ResponseEntity.ok(ApiResponse.success(url));
    }
}