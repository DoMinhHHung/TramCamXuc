package iuh.fit.se.tramcamxuc.modules.marketing.controller;

import iuh.fit.se.tramcamxuc.common.exception.dto.ApiResponse;
import iuh.fit.se.tramcamxuc.modules.marketing.entity.Advertisement;
import iuh.fit.se.tramcamxuc.modules.marketing.service.AdDecisionService;
import iuh.fit.se.tramcamxuc.modules.marketing.service.AdvertisementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/ads/check")
@RequiredArgsConstructor
public class AdDecisionController {

    private final AdDecisionService adDecisionService;
    private final AdvertisementService advertisementService;

    // Client gọi API này TRƯỚC khi play bài hát
    @GetMapping("/status")
    public ResponseEntity<ApiResponse<Map<String, Object>>> checkAdStatus() {
        boolean mustWatchAd = adDecisionService.shouldPlayAd();
        Map<String, Object> response = new HashMap<>();
        response.put("mustWatchAd", mustWatchAd);

        if (mustWatchAd) {
            // Nếu phải xem, trả về luôn link quảng cáo để Client play
            Advertisement ad = advertisementService.getRandomAudioAd();
            response.put("adData", ad);
        }

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // Client gọi API này sau khi play xong bài nhạc (để tăng counter)
    @PostMapping("/song-finished")
    public ResponseEntity<ApiResponse<String>> onSongFinished() {
        adDecisionService.incrementSongCount();
        return ResponseEntity.ok(ApiResponse.success("Counted"));
    }

    // Client gọi API này sau khi QUẢNG CÁO chạy xong (để reset counter)
    @PostMapping("/ad-finished")
    public ResponseEntity<ApiResponse<String>> onAdFinished() {
        adDecisionService.resetAfterAdPlayed();
        return ResponseEntity.ok(ApiResponse.success("Reset counters. Enjoy music!"));
    }
}