package iuh.fit.se.tramcamxuc.modules.music.song.controller;

import iuh.fit.se.tramcamxuc.common.exception.dto.ApiResponse;
import iuh.fit.se.tramcamxuc.modules.music.song.dto.request.*;
import iuh.fit.se.tramcamxuc.modules.music.song.dto.response.SongResponse;
import iuh.fit.se.tramcamxuc.modules.music.song.service.impl.SongServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/songs")
@RequiredArgsConstructor
public class SongController {

    private final SongServiceImpl songService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<SongResponse>> uploadSong(@ModelAttribute @Valid CreateSongRequest request) {
        return ResponseEntity.ok(ApiResponse.success(songService.uploadSong(request)));
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<SongResponse>> updateSong(
            @PathVariable UUID id,
            @ModelAttribute @Valid UpdateSongRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(songService.updateSong(id, request)));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<SongResponse>> changeStatus(
            @PathVariable UUID id,
            @RequestBody @Valid ChangeSongStatusRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(songService.changeSongStatus(id, request.getStatus())));
    }

    @GetMapping("/{slug}")
    public ResponseEntity<ApiResponse<SongResponse>> getSongBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(ApiResponse.success(songService.getSongBySlug(slug)));
    }

    //    @GetMapping("/search")
//    public ResponseEntity<ApiResponse<List<SongResponse>>> searchSongs(@RequestParam String keyword) {
//        return ResponseEntity.ok(ApiResponse.success(songService.searchSongs(keyword)));
//    }
    @GetMapping
    public ResponseEntity<ApiResponse<List<SongResponse>>> search(@RequestParam String keyword) {
        return ResponseEntity.ok(ApiResponse.success(songService.searchSongs(keyword)));
    }

    @GetMapping("/history")
    public ResponseEntity<ApiResponse<List<String>>> getHistory() {
        return ResponseEntity.ok(ApiResponse.success(songService.getSearchHistory()));
    }

    @DeleteMapping("/history")
    public ResponseEntity<ApiResponse<String>> deleteHistoryItem(@RequestParam String keyword) {
        songService.deleteSearchHistory(keyword);
        return ResponseEntity.ok(ApiResponse.success("Deleted"));
    }
}