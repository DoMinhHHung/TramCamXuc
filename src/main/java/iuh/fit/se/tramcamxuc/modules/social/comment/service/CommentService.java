package iuh.fit.se.tramcamxuc.modules.social.comment.service;

import iuh.fit.se.tramcamxuc.modules.social.comment.dto.request.CreateCommentRequest;
import iuh.fit.se.tramcamxuc.modules.social.comment.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface CommentService {
    Comment createComment(CreateCommentRequest request);

    Page<Comment> getCommentsBySong(UUID songId, Pageable pageable);
}
