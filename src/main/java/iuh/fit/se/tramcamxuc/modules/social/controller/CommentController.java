package iuh.fit.se.tramcamxuc.modules.social.controller;

import iuh.fit.se.tramcamxuc.modules.social.dto.request.CreateCommentRequest;
import iuh.fit.se.tramcamxuc.modules.social.entity.Comment;
import iuh.fit.se.tramcamxuc.modules.social.service.CommentService;
import iuh.fit.se.tramcamxuc.modules.social.service.LikeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;
    private final LikeService likeService;

    @PostMapping
    public ResponseEntity<Comment> createComment(@RequestBody @Valid CreateCommentRequest request) {
        return ResponseEntity.ok(commentService.createComment(request));
    }

    @GetMapping("/song/{songId}")
    public ResponseEntity<Page<Comment>> getComments(
            @PathVariable UUID songId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(commentService.getCommentsBySong(songId, PageRequest.of(page, size)));
    }
}