package iuh.fit.se.tramcamxuc.modules.marketing.dto.request;

import iuh.fit.se.tramcamxuc.modules.marketing.entity.Advertisement;
import iuh.fit.se.tramcamxuc.modules.marketing.entity.enums.AdType;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Getter
@Setter
public class CreateAdRequest {
    private String title;
    private String targetUrl;

    @NotNull(message = "File quảng cáo không được thiếu")
    private MultipartFile file;

    @NotNull(message = "Loại quảng cáo phải có")
    private AdType adType;

    private Integer durationSeconds;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Integer priority;
}