package iuh.fit.se.tramcamxuc.modules.music.entity;

import iuh.fit.se.tramcamxuc.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "genres")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Genre extends BaseEntity {
    @Column(unique = true, nullable = false)
    private String name;

    private String slug;
    private String description;
}