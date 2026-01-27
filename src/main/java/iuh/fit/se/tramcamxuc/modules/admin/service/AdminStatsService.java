package iuh.fit.se.tramcamxuc.modules.admin.service;

import iuh.fit.se.tramcamxuc.modules.admin.dto.ChartData;
import iuh.fit.se.tramcamxuc.modules.admin.dto.DashboardStats;
import iuh.fit.se.tramcamxuc.modules.admin.dto.RevenueStatsResponse;

import java.util.List;

public interface AdminStatsService {
    DashboardStats getOverview();

    List<ChartData> getListeningChart(int days);

    List<ChartData> getGenreDistribution();

    RevenueStatsResponse getRevenueStats();
}