package iuh.fit.se.tramcamxuc.modules.music.song.service.impl;

import iuh.fit.se.tramcamxuc.modules.music.song.repository.SongRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class LikeSyncService {
    private final StringRedisTemplate redisTemplate;
    private final SongRepository songRepository;
    private static final String DIRTY_LIKES_KEY = "song:likes:dirty";

    @Scheduled(fixedDelay = 60000)
    @Transactional
    public void syncLikesToDatabase() {
        Set<String> dirtySongIds = redisTemplate.opsForSet().members(DIRTY_LIKES_KEY);
        if (dirtySongIds == null || dirtySongIds.isEmpty()) return;

        log.info("Syncing likes for {} songs...", dirtySongIds.size());
        for (String songIdStr : dirtySongIds) {
            try {
                String key = "song:likes:" + songIdStr;
                Long count = redisTemplate.opsForSet().size(key);
                songRepository.updateLikeCount(UUID.fromString(songIdStr), count != null ? count : 0L);
                redisTemplate.opsForSet().remove(DIRTY_LIKES_KEY, songIdStr);
            } catch (Exception e) {
                log.error("Failed to sync like for song {}", songIdStr);
            }
        }
    }
}