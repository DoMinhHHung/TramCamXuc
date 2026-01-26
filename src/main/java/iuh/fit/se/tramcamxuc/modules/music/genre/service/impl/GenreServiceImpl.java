package iuh.fit.se.tramcamxuc.modules.music.genre.service.impl;

import iuh.fit.se.tramcamxuc.common.exception.AppException;
import iuh.fit.se.tramcamxuc.common.exception.ResourceNotFoundException;
import iuh.fit.se.tramcamxuc.common.utils.SlugUtils;
import iuh.fit.se.tramcamxuc.modules.music.genre.dto.request.GenreRequest;
import iuh.fit.se.tramcamxuc.modules.music.genre.entity.Genre;
import iuh.fit.se.tramcamxuc.modules.music.genre.repository.GenreRepository;
import iuh.fit.se.tramcamxuc.modules.music.genre.service.GenreService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GenreServiceImpl implements GenreService {

    private final GenreRepository genreRepository;

    @Override
    @Cacheable(value = "genres")
    public List<Genre> getAllGenres() {
        return genreRepository.findAll();
    }

    @Override
    @Transactional
    @CacheEvict(value = "genres", allEntries = true)
    public Genre createGenre(GenreRequest request) {
        if (genreRepository.existsByName(request.getName())) {
            throw new AppException("Genre name already exists");
        }

        Genre genre = Genre.builder()
                .name(request.getName())
                .slug(SlugUtils.toSlug(request.getName()))
                .description(request.getDescription())
                .build();

        return genreRepository.save(genre);
    }

    @Override
    @Transactional
    @CacheEvict(value = "genres", allEntries = true)
    public Genre updateGenre(UUID id, GenreRequest request) {
        Genre genre = genreRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Genre not found"));

        if (!genre.getName().equals(request.getName()) && genreRepository.existsByName(request.getName())) {
            throw new AppException("Genre name already exists");
        }

        genre.setName(request.getName());
        genre.setSlug(SlugUtils.toSlug(request.getName()));
        genre.setDescription(request.getDescription());

        return genreRepository.save(genre);
    }

    @Override
    @Transactional
    @CacheEvict(value = "genres", allEntries = true)
    public void deleteGenre(UUID id) {
        if (!genreRepository.existsById(id)) {
            throw new ResourceNotFoundException("Genre not found");
        }

        if (genreRepository.hasSongs(id)) {
            throw new AppException("Cannot delete genre with associated songs");
        }

        genreRepository.deleteById(id);
    }
}