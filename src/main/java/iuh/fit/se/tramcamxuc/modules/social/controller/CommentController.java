package iuh.fit.se.tramcamxuc.modules.social.controller;

import iuh.fit.se.tramcamxuc.common.exception.dto.ApiResponse;
import iuh.fit.se.tramcamxuc.modules.social.dto.request.CreateCommentRequest;
import iuh.fit.se.tramcamxuc.modules.social.entity.Comment;
import iuh.fit.se.tramcamxuc.modules.social.service.CommentService;
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

    @PostMapping
    public ResponseEntity<ApiResponse<Comment>> createComment(@RequestBody @Valid CreateCommentRequest request) {
        return ResponseEntity.ok(ApiResponse.success(commentService.createComment(request)));
    }

    @GetMapping("/song/{songId}")
    public ResponseEntity<ApiResponse<Page<Comment>>> getComments(
            @PathVariable UUID songId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(ApiResponse.success(commentService.getCommentsBySong(songId, PageRequest.of(page, size))));
    }
}