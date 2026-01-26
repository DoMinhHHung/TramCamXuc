package iuh.fit.se.tramcamxuc.modules.subscription.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class CreatePlanRequest {
    @NotBlank(message = "Tên gói không được để trống")
    private String name;

    private String description;

    @NotNull(message = "Giá không được để trống")
    @Min(value = 0, message = "Giá không được âm")
    private Double price;

    @Min(value = 0, message = "Thời hạn không hợp lệ")
    private Integer durationDays;

    private Map<String, Object> features;
}