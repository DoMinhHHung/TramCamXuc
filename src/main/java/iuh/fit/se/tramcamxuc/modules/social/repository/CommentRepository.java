package iuh.fit.se.tramcamxuc.modules.social.repository;

import iuh.fit.se.tramcamxuc.modules.social.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CommentRepository extends MongoRepository<Comment, String> {
    Page<Comment> findBySongIdOrderByCreatedAtDesc(UUID songId, Pageable pageable);
}