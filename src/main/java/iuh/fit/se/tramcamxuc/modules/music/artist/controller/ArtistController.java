package iuh.fit.se.tramcamxuc.modules.music.artist.controller;

import iuh.fit.se.tramcamxuc.modules.music.artist.dto.request.CreateArtistRequest;
import iuh.fit.se.tramcamxuc.modules.music.artist.entity.Artist;
import iuh.fit.se.tramcamxuc.modules.music.artist.service.ArtistService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/artists")
@RequiredArgsConstructor
public class ArtistController {
    private final ArtistService artistService;

    @PostMapping("/system")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Artist> createSystemArtist(@RequestBody @Valid CreateArtistRequest request) {
        return ResponseEntity.ok(artistService.createSystemArtist(request));
    }

    @PostMapping("/register")
    public ResponseEntity<Artist> registerAsArtist(@RequestBody @Valid CreateArtistRequest request) {
        return ResponseEntity.ok(artistService.registerAsArtist(request));
    }
}
