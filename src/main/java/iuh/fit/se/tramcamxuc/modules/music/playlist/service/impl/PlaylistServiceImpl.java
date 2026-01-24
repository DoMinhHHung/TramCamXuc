package iuh.fit.se.tramcamxuc.modules.music.playlist.service.impl;

import iuh.fit.se.tramcamxuc.common.exception.AppException;
import iuh.fit.se.tramcamxuc.common.exception.ResourceNotFoundException;
import iuh.fit.se.tramcamxuc.common.utils.SlugUtils;
import iuh.fit.se.tramcamxuc.modules.music.playlist.dto.request.AddSongRequest;
import iuh.fit.se.tramcamxuc.modules.music.playlist.dto.request.CreatePlaylistRequest;
import iuh.fit.se.tramcamxuc.modules.music.playlist.dto.response.PlaylistResponse;
import iuh.fit.se.tramcamxuc.modules.music.playlist.entity.Playlist;
import iuh.fit.se.tramcamxuc.modules.music.playlist.entity.PlaylistSong;
import iuh.fit.se.tramcamxuc.modules.music.playlist.entity.PlaylistSongId;
import iuh.fit.se.tramcamxuc.modules.music.playlist.repository.*;
import iuh.fit.se.tramcamxuc.modules.music.playlist.service.PlaylistService;
import iuh.fit.se.tramcamxuc.modules.music.song.entity.Song;
import iuh.fit.se.tramcamxuc.modules.music.song.repository.SongRepository;
import iuh.fit.se.tramcamxuc.modules.user.entity.User;
import iuh.fit.se.tramcamxuc.modules.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlaylistServiceImpl implements PlaylistService {
    private final PlaylistRepository playlistRepository;
    private final PlaylistSongRepository playlistSongRepository;
    private final SongRepository songRepository;
    private final UserService userService;


    @Override
    @Transactional
    public PlaylistResponse createPlaylist(CreatePlaylistRequest request) {
        User currentUser = userService.getCurrentUser();

        String slug = SlugUtils.generateSlug(request.getTitle());

        Playlist playlist = Playlist.builder()
                .title(request.getTitle())
                .slug(slug)
                .isPublic(request.isPublic())
                .user(currentUser)
                .build();

        return PlaylistResponse.fromEntity(playlistRepository.save(playlist));
    }

    @Override
    @Transactional
    public void addSongToPlaylist(UUID playlistId, AddSongRequest request) {
        User currentUser = userService.getCurrentUser();

        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new ResourceNotFoundException("Playlist not found"));

        if (!playlist.getUser().getId().equals(currentUser.getId())) {
            throw new AppException("Don't have permission to modify this playlist");
        }

        Song song = songRepository.findById(request.getSongId())
                .orElseThrow(() -> new ResourceNotFoundException("Playlist not found"));

        if (playlistSongRepository.existsByPlaylistIdAndSongId(playlistId, request.getSongId())) {
            throw new AppException("This song is already in the playlist");
        }

        PlaylistSong playlistSong = PlaylistSong.builder()
                .id(new PlaylistSongId(playlistId, request.getSongId()))
                .playlist(playlist)
                .song(song)
                .addedAt(LocalDateTime.now())
                .build();

        playlistSongRepository.save(playlistSong);

        if (playlist.getCoverUrl() == null || playlist.getCoverUrl().isEmpty()) {
            playlist.setCoverUrl(song.getCoverUrl());
            playlistRepository.save(playlist);
        }
    }

    @Override
    @Transactional
    public void removeSongFromPlaylist(UUID playlistId, UUID songId) {
        User currentUser = userService.getCurrentUser();
        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new ResourceNotFoundException("Playlist not found"));

        if (!playlist.getUser().getId().equals(currentUser.getId())) {
            throw new AppException("Don't have permission to modify this playlist");
        }

        playlistSongRepository.deleteByPlaylistIdAndSongId(playlistId, songId);
    }

    @Override
    public PlaylistResponse getPlaylistBySlug(String slug) {
        Playlist playlist = playlistRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Playlist not found"));

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isOwner = false;

        if (auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken)) {
            String currentUserEmail = auth.getName();
            if (currentUserEmail.equals(playlist.getUser().getEmail())) {
                isOwner = true;
            }
        }
        if (!playlist.isPublic() && !isOwner) {
            throw new AppException("Playlist is private. You don't have permission to access this playlist.");
        }

        return PlaylistResponse.fromEntity(playlist);
    }

    @Override
    public List<PlaylistResponse> getMyPlaylists() {
        User currentUser = userService.getCurrentUser();
        List<Playlist> playlists = playlistRepository.findByUserId(currentUser.getId());
        return playlists.stream().map(PlaylistResponse::fromEntity).collect(Collectors.toList());
    }
}
