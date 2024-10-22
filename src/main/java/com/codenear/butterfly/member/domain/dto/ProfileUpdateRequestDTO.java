package com.codenear.butterfly.member.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class ProfileUpdateRequestDTO {
    @Schema(description = "닉네임", example = "codenear")
    @Size(min = 2, max = 8, message = "닉네임은 2자 ~ 8자 사이여야 합니다.")
    @NotNull
    private String nickname;

    @Schema(description = "프로필 이미지")
    private MultipartFile profileImage;
}
