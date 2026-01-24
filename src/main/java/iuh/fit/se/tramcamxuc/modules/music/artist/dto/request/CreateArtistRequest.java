package iuh.fit.se.tramcamxuc.modules.music.artist.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateArtistRequest {
    @NotBlank(message = "Tên nghệ sĩ không được để trống")
    private String name;
    private String bio;
    private String imageUrl;
}