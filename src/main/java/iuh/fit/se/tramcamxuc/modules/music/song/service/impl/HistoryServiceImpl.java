package iuh.fit.se.tramcamxuc.modules.music.song.service.impl;

import iuh.fit.se.tramcamxuc.common.exception.ResourceNotFoundException;
import iuh.fit.se.tramcamxuc.modules.music.song.dto.request.LogHistoryRequest;
import iuh.fit.se.tramcamxuc.modules.music.song.entity.ListeningHistory;
import iuh.fit.se.tramcamxuc.modules.music.song.entity.Song;
import iuh.fit.se.tramcamxuc.modules.music.song.repository.ListeningHistoryRepository;
import iuh.fit.se.tramcamxuc.modules.music.song.repository.SongRepository;
import iuh.fit.se.tramcamxuc.modules.music.song.service.HistoryService;
import iuh.fit.se.tramcamxuc.modules.music.song.service.SongService;
import iuh.fit.se.tramcamxuc.modules.user.entity.User;
import iuh.fit.se.tramcamxuc.modules.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class HistoryServiceImpl implements HistoryService {
    private final ListeningHistoryRepository historyRepository;
    private final UserService userService;
    private final SongRepository songRepository;
    private final SongService songService;

    @Override
    public void logHistory(LogHistoryRequest request) {
        User currentUser = userService.getCurrentUser();

        Song song = songRepository.findById(request.getSongId())
                .orElseThrow(() -> new ResourceNotFoundException("Song not found"));

        ListeningHistory history = ListeningHistory.builder()
                .userId(currentUser.getId())
                .songId(song.getId())
                .songTitle(song.getTitle())
                .songCoverUrl(song.getCoverUrl())
                .artistName(song.getArtist().getName())
                .artistSlug(song.getArtist().getSlug())
                .songSlug(song.getSlug())
                .listenedAt(LocalDateTime.now())
                .listenedSeconds(request.getListenedSeconds())
                .build();

        historyRepository.save(history);
        songService.incrementListeningCount(song.getId());

    }

    @Override
    public Page<ListeningHistory> getMyHistory(int page, int size) {
        User currentUser = userService.getCurrentUser();
        return historyRepository.findByUserIdOrderByListenedAtDesc(
                currentUser.getId(),
                PageRequest.of(page, size)
        );
    }
}
