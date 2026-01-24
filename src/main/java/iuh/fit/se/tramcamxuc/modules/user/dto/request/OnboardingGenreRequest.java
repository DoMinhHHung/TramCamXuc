package iuh.fit.se.tramcamxuc.modules.user.dto.request;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
public class OnboardingGenreRequest {
    @Size(min = 1, max = 5, message = "Vui lòng chọn từ 1 đến 5 thể loại nhạc")
    private Set<UUID> genreIds;
}