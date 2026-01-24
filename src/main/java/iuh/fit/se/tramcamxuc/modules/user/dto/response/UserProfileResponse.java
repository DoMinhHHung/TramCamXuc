package iuh.fit.se.tramcamxuc.modules.user.dto.response;

import iuh.fit.se.tramcamxuc.modules.user.entity.User;
import iuh.fit.se.tramcamxuc.modules.music.entity.Genre;
import lombok.*;
import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
public class UserProfileResponse {
    private String id;
    private String email;
    private String username;
    private String fullName;
    private String avatarUrl;
    private LocalDate dob;
    private String gender;
    private Set<String> favoriteGenres;

    public static UserProfileResponse fromEntity(User user) {
        return UserProfileResponse.builder()
                .id(user.getId().toString())
                .email(user.getEmail())
                .username(user.getUsername())
                .fullName(user.getFullName())
                .avatarUrl(user.getAvatarUrl())
                .dob(user.getDob())
                .gender(user.getGender() != null ? user.getGender().name() : null)
                .favoriteGenres(user.getFavoriteGenres() != null
                        ? user.getFavoriteGenres().stream().map(Genre::getName).collect(Collectors.toSet())
                        : Set.of())
                .build();
    }
}