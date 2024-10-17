package com.codenear.butterfly.member.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class ProfileUpdateRequestDTO {
    @Schema(description = "닉네임", example = "codenear")
    @Size(min = 2, max = 8, message = "닉네임은 2자 ~ 8자 사이여야 합니다.")
    @NotNull
    private String nickname;

    // 프로필 이미지
}
