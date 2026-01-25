package iuh.fit.se.tramcamxuc.modules.social.service;

import java.util.Set;
import java.util.UUID;

public interface LikeService {
    boolean toggleLike(UUID songId);

    Long countLikes(UUID songId);

    boolean hasLiked(UUID songId);

    Set<String> getLikedSongIds();
}
