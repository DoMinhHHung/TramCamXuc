package iuh.fit.se.tramcamxuc.modules.music.song.repository;

import iuh.fit.se.tramcamxuc.modules.music.song.entity.ListeningHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ListeningHistoryRepository extends MongoRepository<ListeningHistory, String> {
    Page<ListeningHistory> findByUserIdOrderByListenedAtDesc(UUID userId, Pageable pageable);
}