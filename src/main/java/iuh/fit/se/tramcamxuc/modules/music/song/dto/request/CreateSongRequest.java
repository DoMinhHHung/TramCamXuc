package iuh.fit.se.tramcamxuc.modules.music.song.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
public class CreateSongRequest {
    @NotBlank(message = "Tên bài hát không được để trống")
    private String title;

    private String lyric;

    private MultipartFile audioFile;

    private MultipartFile coverFile;

    private Set<UUID> genreIds;
}