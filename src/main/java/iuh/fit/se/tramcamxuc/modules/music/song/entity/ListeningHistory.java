package iuh.fit.se.tramcamxuc.modules.music.song.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.UUID;

@Document(collection = "listening_history")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ListeningHistory {

    @Id
    private String id;

    @Indexed
    private UUID userId;

    @Indexed
    private UUID songId;

    private String songTitle;
    private String songCoverUrl;
    private String artistName;
    private String artistSlug;
    private String songSlug;

    private LocalDateTime listenedAt;

    private Integer listenedSeconds;
}