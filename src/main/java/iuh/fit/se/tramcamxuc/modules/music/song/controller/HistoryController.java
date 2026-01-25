package iuh.fit.se.tramcamxuc.modules.music.song.controller;

import iuh.fit.se.tramcamxuc.modules.music.song.dto.request.LogHistoryRequest;
import iuh.fit.se.tramcamxuc.modules.music.song.service.HistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/history")
@RequiredArgsConstructor
public class HistoryController {

    private final HistoryService historyService;

    @PostMapping
    public ResponseEntity<String> logHistory(@RequestBody LogHistoryRequest request) {
        historyService.logHistory(request);
        return ResponseEntity.ok("Logged");
    }

    @GetMapping
    public ResponseEntity<?> getMyHistory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return ResponseEntity.ok(historyService.getMyHistory(page, size));
    }
}