package iuh.fit.se.tramcamxuc.modules.marketing.service;

import iuh.fit.se.tramcamxuc.modules.subscription.repository.UserSubscriptionRepository;
import iuh.fit.se.tramcamxuc.modules.user.entity.User;
import iuh.fit.se.tramcamxuc.modules.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdDecisionService {

    private final StringRedisTemplate redisTemplate;
    private final UserService userService;
    private final UserSubscriptionRepository userSubscriptionRepository;

    private static final int SONG_LIMIT = 5;
    private static final long TIME_LIMIT_MS = 30 * 60 * 1000;


    public boolean shouldPlayAd() {
        User currentUser = userService.getCurrentUser();
        UUID userId = currentUser.getId();


        boolean isPremium = userSubscriptionRepository.isPremiumUser(userId);
        if (isPremium) {
            return false;
        }

        String countKey = "ads:counter:" + userId;
        String timeKey = "ads:last_time:" + userId;

        String countStr = redisTemplate.opsForValue().get(countKey);
        String lastTimeStr = redisTemplate.opsForValue().get(timeKey);

        int currentCount = (countStr == null) ? 0 : Integer.parseInt(countStr);
        long lastTime = (lastTimeStr == null) ? 0 : Long.parseLong(lastTimeStr);
        long currentTime = System.currentTimeMillis();

        boolean countCondition = currentCount >= SONG_LIMIT;
        boolean timeCondition = (currentTime - lastTime) >= TIME_LIMIT_MS;

        if (lastTime == 0) {
            resetAdTimer(userId);
            return false;
        }

        return countCondition || timeCondition;
    }

    public void incrementSongCount() {
        User currentUser = userService.getCurrentUser();
        String key = "ads:counter:" + currentUser.getId();
        redisTemplate.opsForValue().increment(key);
    }

    public void resetAfterAdPlayed() {
        User currentUser = userService.getCurrentUser();
        UUID userId = currentUser.getId();

        String countKey = "ads:counter:" + userId;
        String timeKey = "ads:last_time:" + userId;

        redisTemplate.delete(countKey);

        redisTemplate.opsForValue().set(timeKey, String.valueOf(System.currentTimeMillis()));
    }

    private void resetAdTimer(UUID userId) {
        String timeKey = "ads:last_time:" + userId;
        redisTemplate.opsForValue().setIfAbsent(timeKey, String.valueOf(System.currentTimeMillis()));
    }
}