package iuh.fit.se.tramcamxuc.modules.music.song.controller;

import iuh.fit.se.tramcamxuc.common.exception.dto.ApiResponse;
import iuh.fit.se.tramcamxuc.modules.music.song.dto.request.LogHistoryRequest;
import iuh.fit.se.tramcamxuc.modules.music.song.entity.ListeningHistory;
import iuh.fit.se.tramcamxuc.modules.music.song.service.HistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/history")
@RequiredArgsConstructor
public class HistoryController {

    private final HistoryService historyService;

    @PostMapping
    public ResponseEntity<ApiResponse<String>> logHistory(@RequestBody LogHistoryRequest request) {
        historyService.logHistory(request);
        return ResponseEntity.ok(ApiResponse.success("Logged"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<ListeningHistory>>> getMyHistory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return ResponseEntity.ok(ApiResponse.success(historyService.getMyHistory(page, size)));
    }
}