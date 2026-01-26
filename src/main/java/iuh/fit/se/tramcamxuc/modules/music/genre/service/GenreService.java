package iuh.fit.se.tramcamxuc.modules.music.genre.service;

import iuh.fit.se.tramcamxuc.modules.music.genre.dto.request.GenreRequest;
import iuh.fit.se.tramcamxuc.modules.music.genre.entity.Genre;

import java.util.List;
import java.util.UUID;

public interface GenreService {
    List<Genre> getAllGenres();

    Genre createGenre(GenreRequest request);

    Genre updateGenre(UUID id, GenreRequest request);

    void deleteGenre(UUID id);
}