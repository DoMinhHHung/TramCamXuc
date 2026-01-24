package iuh.fit.se.tramcamxuc.modules.music.artist.entity;

import iuh.fit.se.tramcamxuc.common.BaseEntity;
import iuh.fit.se.tramcamxuc.modules.music.artist.entity.enums.ArtistStatus;
import iuh.fit.se.tramcamxuc.modules.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "artists")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Artist extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String bio;

    private String imageUrl;

    @Column(unique = true)
    private String slug;

    @Enumerated(EnumType.STRING)
    private ArtistStatus status;

    // Phân loại
    // true = System Artist / Verified Label (Up tẹt ga)
    // false = User Artist (Phải mua Premium mới up được)
    @Column(name = "is_verified", nullable = false)
    private boolean isVerified;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true, nullable = true)
    private User user;
}