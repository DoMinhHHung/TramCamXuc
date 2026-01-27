package iuh.fit.se.tramcamxuc.modules.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RevenueByPlanDTO {
    private String planName;
    private Double amount;
    private Long transactionCount;
}