package iuh.fit.se.tramcamxuc.modules.admin.controller;

import iuh.fit.se.tramcamxuc.modules.music.song.dto.response.SongResponse;
import iuh.fit.se.tramcamxuc.modules.music.song.entity.Song;
import iuh.fit.se.tramcamxuc.modules.music.song.entity.enums.SongStatus;
import iuh.fit.se.tramcamxuc.modules.music.song.repository.SongRepository;
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
    private final SongRepository songRepository;

    @GetMapping("/pending")
    public ResponseEntity<Page<SongResponse>> getPendingSongs(
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        Page<Song> songPage = songRepository.findByStatus(SongStatus.PENDING_APPROVAL, pageable);
        Page<SongResponse> responsePage = songPage.map(SongResponse::fromEntity);

        return ResponseEntity.ok(responsePage);
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<String> approveSong(@PathVariable UUID id) {
        songService.approveSong(id);
        return ResponseEntity.ok("Approved the song.");
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<String> rejectSong(@PathVariable UUID id, @RequestParam String reason) {
        songService.rejectSong(id, reason);
        return ResponseEntity.ok("Rejected the song.");
    }
}
