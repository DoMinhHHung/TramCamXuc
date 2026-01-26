package iuh.fit.se.tramcamxuc.modules.music.genre.controller;

import iuh.fit.se.tramcamxuc.common.exception.dto.ApiResponse;
import iuh.fit.se.tramcamxuc.modules.music.genre.dto.request.GenreRequest;
import iuh.fit.se.tramcamxuc.modules.music.genre.entity.Genre;
import iuh.fit.se.tramcamxuc.modules.music.genre.service.GenreService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/genres")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminGenreController {

    private final GenreService genreService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Genre>>> getAll() {
        return ResponseEntity.ok(ApiResponse.success(genreService.getAllGenres()));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Genre>> create(@RequestBody @Valid GenreRequest request) {
        return ResponseEntity.ok(ApiResponse.success(genreService.createGenre(request)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Genre>> update(
            @PathVariable UUID id,
            @RequestBody @Valid GenreRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(genreService.updateGenre(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> delete(@PathVariable UUID id) {
        genreService.deleteGenre(id);
        return ResponseEntity.ok(ApiResponse.success("Xóa thể loại thành công"));
    }
}