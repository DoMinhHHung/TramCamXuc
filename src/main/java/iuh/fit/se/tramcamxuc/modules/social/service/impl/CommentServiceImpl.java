package iuh.fit.se.tramcamxuc.modules.social.service.impl;

import iuh.fit.se.tramcamxuc.common.exception.ResourceNotFoundException;
import iuh.fit.se.tramcamxuc.modules.music.song.repository.SongRepository;
import iuh.fit.se.tramcamxuc.modules.social.dto.request.CreateCommentRequest;
import iuh.fit.se.tramcamxuc.modules.social.entity.Comment;
import iuh.fit.se.tramcamxuc.modules.social.repository.CommentRepository;
import iuh.fit.se.tramcamxuc.modules.social.service.CommentService;
import iuh.fit.se.tramcamxuc.modules.user.entity.User;
import iuh.fit.se.tramcamxuc.modules.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final UserService userService;
    private final SongRepository songRepository;

    public Comment createComment(CreateCommentRequest request) {
        User currentUser = userService.getCurrentUser();

        if (!songRepository.existsById(request.getSongId())) {
            throw new ResourceNotFoundException("This song does not exist");
        }

        Comment comment = Comment.builder()
                .songId(request.getSongId())
                .content(request.getContent())
                .userId(currentUser.getId())
                .userFullName(currentUser.getFullName())
                .userAvatarUrl(currentUser.getAvatarUrl())
                .createdAt(LocalDateTime.now())
                .build();

        return commentRepository.save(comment);
    }

    @Override
    public Page<Comment> getCommentsBySong(UUID songId, Pageable pageable) {
        return commentRepository.findBySongIdOrderByCreatedAtDesc(songId, pageable);
    }
}
