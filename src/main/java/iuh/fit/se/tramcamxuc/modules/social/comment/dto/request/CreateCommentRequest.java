package iuh.fit.se.tramcamxuc.modules.social.comment.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateCommentRequest {
    @NotNull(message = "Bài hát không được thiếu")
    private UUID songId;

    @NotBlank(message = "Nội dung không được để trống")
    private String content;
}