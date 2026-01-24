package iuh.fit.se.tramcamxuc.modules.music.album.service.impl;

import iuh.fit.se.tramcamxuc.common.exception.AppException;
import iuh.fit.se.tramcamxuc.common.exception.ResourceNotFoundException;
import iuh.fit.se.tramcamxuc.common.utils.SlugUtils;
import iuh.fit.se.tramcamxuc.modules.music.album.dto.request.AddSongToAlbumRequest;
import iuh.fit.se.tramcamxuc.modules.music.album.dto.request.CreateAlbumRequest;
import iuh.fit.se.tramcamxuc.modules.music.album.dto.response.AlbumResponse;
import iuh.fit.se.tramcamxuc.modules.music.album.entity.Album;
import iuh.fit.se.tramcamxuc.modules.music.album.repository.AlbumRepository;
import iuh.fit.se.tramcamxuc.modules.music.artist.entity.Artist;
import iuh.fit.se.tramcamxuc.modules.music.artist.repository.ArtistRepository;
import iuh.fit.se.tramcamxuc.modules.music.song.entity.Song;
import iuh.fit.se.tramcamxuc.modules.music.song.repository.SongRepository;
import iuh.fit.se.tramcamxuc.modules.user.entity.User;
import iuh.fit.se.tramcamxuc.modules.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AlbumServiceImpl {

    private final AlbumRepository albumRepository;
    private final ArtistRepository artistRepository;
    private final SongRepository songRepository;
    private final UserService userService;

    @Transactional
    public AlbumResponse createAlbum(CreateAlbumRequest request) {
        User currentUser = userService.getCurrentUser();
        Artist artist = artistRepository.findByUserId(currentUser.getId())
                .orElseThrow(() -> new AppException("You don't have permission to create album. Please create an artist profile first."));

        String slug = SlugUtils.generateSlug(request.getTitle());

        Album album = Album.builder()
                .title(request.getTitle())
                .slug(slug)
                .description(request.getDescription())
                .coverUrl(request.getCoverUrl())
                .releaseDate(request.getReleaseDate())
                .totalDuration(0)
                .artist(artist)
                .build();

        return AlbumResponse.fromEntity(albumRepository.save(album));
    }

    @Transactional
    public void addSongsToAlbum(UUID albumId, AddSongToAlbumRequest request) {
        User currentUser = userService.getCurrentUser();
        Artist artist = artistRepository.findByUserId(currentUser.getId())
                .orElseThrow(() -> new AppException("Artist profile not exist."));

        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new ResourceNotFoundException("Album not exist."));

        // CHECK QUYỀN: Album này có phải của ông Artist này không?
        if (!album.getArtist().getId().equals(artist.getId())) {
            throw new AppException("You don't have permission to modify this album!");
        }

        List<Song> songs = songRepository.findAllById(request.getSongIds());

        // Update logic
        for (Song song : songs) {
            if (song.getArtist().getId().equals(artist.getId())) {
                song.setAlbum(album);
                album.setTotalDuration(album.getTotalDuration() + song.getDuration());
            }
        }

        songRepository.saveAll(songs);
        albumRepository.save(album);
    }

    public AlbumResponse getAlbumBySlug(String slug) {
        Album album = albumRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Album not found"));
        return AlbumResponse.fromEntity(album);
    }
}