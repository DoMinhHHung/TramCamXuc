package iuh.fit.se.tramcamxuc.modules.music.playlist.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreatePlaylistRequest {
    @NotBlank(message = "Tiêu đề không được để trống")
    private String title;
    private boolean isPublic;
}