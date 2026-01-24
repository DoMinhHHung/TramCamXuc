package iuh.fit.se.tramcamxuc.modules.music.song.dto.request;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;
import java.util.UUID;

@Getter
@Setter
public class UpdateSongRequest {
    private String title;

    private String lyric;

    private MultipartFile coverFile;

    private Set<UUID> genreIds;
}