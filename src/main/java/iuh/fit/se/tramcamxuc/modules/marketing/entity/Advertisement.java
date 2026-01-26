package iuh.fit.se.tramcamxuc.modules.marketing.entity;

import iuh.fit.se.tramcamxuc.common.BaseEntity;
import iuh.fit.se.tramcamxuc.modules.marketing.entity.enums.AdType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "advertisements")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Advertisement extends BaseEntity {

    private String title;

    private String mediaUrl;

    private String targetUrl;

    @Enumerated(EnumType.STRING)
    private AdType adType;

    private Integer durationSeconds;

    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private boolean isActive;
    private Integer priority;


}