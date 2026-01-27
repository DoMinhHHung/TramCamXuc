package iuh.fit.se.tramcamxuc.modules.admin.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class RevenueStatsResponse {
    private Double totalRevenue;
    private List<RevenueByPlanDTO> revenueByPlan;

    @Data
    @Builder
    public static class RevenueByPlan {
        private String planName;
        private Double amount;
        private Long transactionCount;
    }
}