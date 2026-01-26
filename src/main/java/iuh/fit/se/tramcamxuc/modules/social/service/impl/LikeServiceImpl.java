package iuh.fit.se.tramcamxuc.modules.social.service.impl;

import iuh.fit.se.tramcamxuc.modules.social.service.LikeService;
import iuh.fit.se.tramcamxuc.modules.user.entity.User;
import iuh.fit.se.tramcamxuc.modules.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class LikeServiceImpl implements LikeService {
    private final StringRedisTemplate redisTemplate;
    private final UserService userService;

    private static final String SONG_LIKES_KEY = "song:likes:";
    private static final String USER_LIKES_KEY = "user:likes:";

    @Override
    public boolean toggleLike(UUID songId) {
        User currentUser = userService.getCurrentUser();
        String userId = currentUser.getId().toString();
        String sId = songId.toString();

        String songKey = SONG_LIKES_KEY + sId;
        String userKey = USER_LIKES_KEY + userId;

        Boolean isLiked = redisTemplate.opsForSet().isMember(songKey, userId);
        boolean result;

        if (Boolean.TRUE.equals(isLiked)) {
            redisTemplate.opsForSet().remove(songKey, userId);
            redisTemplate.opsForSet().remove(userKey, sId);
            result = false;
        } else {
            redisTemplate.opsForSet().add(songKey, userId);
            redisTemplate.opsForSet().add(userKey, sId);
            result = true;
        }

        redisTemplate.opsForSet().add("song:likes:dirty", sId);

        return result;
    }

    @Override
    public Long countLikes(UUID songId) {
        String key = SONG_LIKES_KEY + songId.toString();
        Long count = redisTemplate.opsForSet().size(key);
        return count != null ? count : 0L;
    }

    @Override
    public boolean hasLiked(UUID songId) {
        try {
            User currentUser = userService.getCurrentUser();
            String key = SONG_LIKES_KEY + songId.toString();
            return Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(key, currentUser.getId().toString()));
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public Set<String> getLikedSongIds() {
        User currentUser = userService.getCurrentUser();
        String userKey = USER_LIKES_KEY + currentUser.getId().toString();
        return redisTemplate.opsForSet().members(userKey);
    }
}
