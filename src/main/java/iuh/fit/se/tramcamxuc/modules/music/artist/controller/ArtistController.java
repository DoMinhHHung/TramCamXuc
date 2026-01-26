package iuh.fit.se.tramcamxuc.modules.music.artist.controller;

import iuh.fit.se.tramcamxuc.common.exception.dto.ApiResponse;
import iuh.fit.se.tramcamxuc.modules.music.artist.dto.request.CreateArtistRequest;
import iuh.fit.se.tramcamxuc.modules.music.artist.dto.response.ArtistResponse;
import iuh.fit.se.tramcamxuc.modules.music.artist.service.ArtistService;
import iuh.fit.se.tramcamxuc.modules.music.song.dto.response.SongResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/artists")
@RequiredArgsConstructor
public class ArtistController {
    private final ArtistService artistService;

    @PostMapping("/system")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ArtistResponse>> createSystemArtist(@RequestBody @Valid CreateArtistRequest request) {
        return ResponseEntity.ok(ApiResponse.success(artistService.createSystemArtist(request)));
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<ArtistResponse>> registerAsArtist(@RequestBody @Valid CreateArtistRequest request) {
        return ResponseEntity.ok(ApiResponse.success(artistService.registerAsArtist(request)));
    }

    @PostMapping("/{id}/follow")
    public ResponseEntity<ApiResponse<String>> follow(@PathVariable UUID id) {
        artistService.followArtist(id);
        return ResponseEntity.ok(ApiResponse.success("Followed success"));
    }

    @DeleteMapping("/{id}/follow")
    public ResponseEntity<ApiResponse<String>> unfollow(@PathVariable UUID id) {
        artistService.unfollowArtist(id);
        return ResponseEntity.ok(ApiResponse.success("Unfollowed success"));
    }

    @GetMapping("/{id}/songs")
    public ResponseEntity<ApiResponse<Page<SongResponse>>> getArtistSongs(
            @PathVariable UUID id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(ApiResponse.success(
                artistService.getArtistSongs(id, page, size)
        ));
    }
}