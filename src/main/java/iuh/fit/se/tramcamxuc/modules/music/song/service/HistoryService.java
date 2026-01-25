package iuh.fit.se.tramcamxuc.modules.music.song.service;

import iuh.fit.se.tramcamxuc.modules.music.song.dto.request.LogHistoryRequest;
import iuh.fit.se.tramcamxuc.modules.music.song.entity.ListeningHistory;
import org.springframework.data.domain.Page;

public interface HistoryService {
    void logHistory(LogHistoryRequest request);

    Page<ListeningHistory> getMyHistory(int page, int size);

}
