package iuh.fit.se.tramcamxuc.modules.music.album.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Getter @Setter
public class CreateAlbumRequest {
    @NotBlank(message = "Tên album không được để trống")
    private String title;

    private String description;
    private String coverUrl;
    private LocalDate releaseDate;
}