package iuh.fit.se.tramcamxuc.modules.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PayOSWebhookDTO {
    private String code;
    private String desc;
    private Boolean success;
    private String signature;
    private Map<String, Object> data;
}