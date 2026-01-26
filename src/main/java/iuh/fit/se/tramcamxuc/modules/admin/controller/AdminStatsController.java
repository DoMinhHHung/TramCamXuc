package iuh.fit.se.tramcamxuc.modules.admin.controller;

import iuh.fit.se.tramcamxuc.common.exception.dto.ApiResponse;
import iuh.fit.se.tramcamxuc.modules.admin.dto.ChartData;
import iuh.fit.se.tramcamxuc.modules.admin.dto.DashboardStats;
import iuh.fit.se.tramcamxuc.modules.admin.service.AdminStatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/stats")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminStatsController {

    private final AdminStatsService statsService;

    @GetMapping("/overview")
    public ResponseEntity<ApiResponse<DashboardStats>> getOverview() {
        return ResponseEntity.ok(ApiResponse.success(statsService.getOverview()));
    }

    @GetMapping("/listening-trend")
    public ResponseEntity<ApiResponse<List<ChartData>>> getChart(
            @RequestParam(defaultValue = "7") int days
    ) {
        return ResponseEntity.ok(ApiResponse.success(statsService.getListeningChart(days)));
    }

    @GetMapping("/genre-distribution")
    public ResponseEntity<ApiResponse<List<ChartData>>> getGenreDistribution() {
        return ResponseEntity.ok(ApiResponse.success(statsService.getGenreDistribution()));
    }
}