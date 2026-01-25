package iuh.fit.se.tramcamxuc.modules.admin.controller;

import iuh.fit.se.tramcamxuc.common.exception.dto.ApiResponse;
import iuh.fit.se.tramcamxuc.modules.music.song.dto.response.SongResponse;
import iuh.fit.se.tramcamxuc.modules.music.song.service.SongService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/admin/songs")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminReviewSongController {
    private final SongService songService;

    @GetMapping("/pending")
    public ResponseEntity<ApiResponse<Page<SongResponse>>> getPendingSongs(
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        return ResponseEntity.ok(ApiResponse.success(songService.getPendingSongs(pageable)));
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<ApiResponse<String>> approveSong(@PathVariable UUID id) {
        songService.approveSong(id);
        return ResponseEntity.ok(ApiResponse.success("Approved the song."));
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<ApiResponse<String>> rejectSong(@PathVariable UUID id, @RequestParam String reason) {
        songService.rejectSong(id, reason);
        return ResponseEntity.ok(ApiResponse.success("Rejected the song."));
    }
}