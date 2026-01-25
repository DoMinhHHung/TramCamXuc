package iuh.fit.se.tramcamxuc.modules.social.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.UUID;

@Document(collection = "comments")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Comment {
    @Id
    private String id;

    @Indexed
    private UUID songId;

    private String content;

    private UUID userId;
    private String userFullName;
    private String userAvatarUrl;

    private LocalDateTime createdAt;
}