package iuh.fit.se.tramcamxuc.modules.music.song.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class LogHistoryRequest {
    private UUID songId;
    private Integer listenedSeconds;
}