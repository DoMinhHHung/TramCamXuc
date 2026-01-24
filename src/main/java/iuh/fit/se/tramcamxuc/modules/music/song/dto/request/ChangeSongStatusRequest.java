package iuh.fit.se.tramcamxuc.modules.music.song.dto.request;

import iuh.fit.se.tramcamxuc.modules.music.song.entity.enums.SongStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangeSongStatusRequest {
    @NotNull(message = "Trạng thái không được để trống")
    private SongStatus status;
}