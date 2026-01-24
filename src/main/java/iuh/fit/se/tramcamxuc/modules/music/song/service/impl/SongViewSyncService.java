package iuh.fit.se.tramcamxuc.modules.music.song.service.impl;

import iuh.fit.se.tramcamxuc.modules.music.song.repository.SongRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class SongViewSyncService {

    private final StringRedisTemplate redisTemplate;
    private final SongRepository songRepository;

    private static final String SONG_VIEW_COUNT_KEY = "song:view:buffer";

    @Scheduled(fixedDelay = 120000)
    @Transactional
    public void syncViewsToDatabase() {
        Map<Object, Object> viewsMap = redisTemplate.opsForHash().entries(SONG_VIEW_COUNT_KEY);

        if (viewsMap.isEmpty()) return;

        log.info("Bắt đầu đồng bộ {} bài hát từ Redis về DB...", viewsMap.size());

        for (Map.Entry<Object, Object> entry : viewsMap.entrySet()) {
            try {
                String songIdStr = (String) entry.getKey();
                Long viewCount = Long.parseLong(entry.getValue().toString());

                songRepository.incrementListeningCount(UUID.fromString(songIdStr), viewCount);

                redisTemplate.opsForHash().delete(SONG_VIEW_COUNT_KEY, songIdStr);

            } catch (Exception e) {
                log.error("Lỗi sync view cho song {}: {}", entry.getKey(), e.getMessage());
            }
        }
        log.info("Đồng bộ hoàn tất.");
    }
}