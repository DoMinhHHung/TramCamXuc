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
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class HistoryServiceImpl implements HistoryService {
    private final ListeningHistoryRepository historyRepository;
    private final UserService userService;
    private final SongRepository songRepository;
    private final SongService songService;
    private final StringRedisTemplate redisTemplate;

    @Override
    public void logHistory(LogHistoryRequest request) {
        User currentUser = userService.getCurrentUser();

        Song song = songRepository.findByIdWithArtist(request.getSongId())
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

        String weekKey = "trending:week:" + java.time.LocalDate.now().get(java.time.temporal.IsoFields.WEEK_OF_WEEK_BASED_YEAR);
        redisTemplate.opsForZSet().incrementScore(weekKey, request.getSongId().toString(), 1);
        redisTemplate.expire(weekKey, java.time.Duration.ofDays(14));

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
