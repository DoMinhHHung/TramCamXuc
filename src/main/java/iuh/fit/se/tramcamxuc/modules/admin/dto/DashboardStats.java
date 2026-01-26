package iuh.fit.se.tramcamxuc.modules.admin.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DashboardStats {
    private long totalUsers;
    private long totalSongs;
    private long totalListens;
    private long totalArtists;
}