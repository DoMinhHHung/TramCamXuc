package iuh.fit.se.tramcamxuc.modules.music.album.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import java.util.List;
import java.util.UUID;

@Getter @Setter
public class AddSongToAlbumRequest {
    @NotEmpty(message = "Danh sách bài hát không được trống")
    private List<UUID> songIds;
}