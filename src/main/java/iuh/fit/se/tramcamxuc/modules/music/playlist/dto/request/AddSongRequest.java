package iuh.fit.se.tramcamxuc.modules.music.playlist.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class AddSongRequest {
    @NotNull(message = "Song ID không được thiếu")
    private UUID songId;
}