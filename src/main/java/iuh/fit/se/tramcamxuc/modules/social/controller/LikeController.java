package iuh.fit.se.tramcamxuc.modules.social.controller;

import iuh.fit.se.tramcamxuc.modules.social.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/likes")
@RequiredArgsConstructor
public class LikeController {
    private final LikeService likeService;

    @PostMapping("/song/{songId}/toggle")
    public ResponseEntity<Map<String, Object>> toggleLike(@PathVariable UUID songId) {
        boolean isLiked = likeService.toggleLike(songId);
        Long totalLikes = likeService.countLikes(songId);

        Map<String, Object> response = new HashMap<>();
        response.put("liked", isLiked);
        response.put("total", totalLikes);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/song/{songId}/check")
    public ResponseEntity<Map<String, Object>> checkLikeStatus(@PathVariable UUID songId) {
        boolean isLiked = likeService.hasLiked(songId);
        Long totalLikes = likeService.countLikes(songId);

        Map<String, Object> response = new HashMap<>();
        response.put("liked", isLiked);
        response.put("total", totalLikes);

        return ResponseEntity.ok(response);
    }
}
