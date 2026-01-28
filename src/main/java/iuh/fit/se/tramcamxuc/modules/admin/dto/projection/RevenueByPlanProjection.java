package iuh.fit.se.tramcamxuc.modules.admin.dto.projection;

public interface RevenueByPlanProjection {
    String getPlanName();
    Double getTotalAmount();
    Long getTransactionCount();
}