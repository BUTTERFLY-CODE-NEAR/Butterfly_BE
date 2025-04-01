package com.codenear.butterfly.auth.domain.dto;

import com.codenear.butterfly.member.domain.Platform;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Schema(title = "소셜 로그인 JSON", description = "소셜 로그인 시 정의되는 JSON 데이터 입니다.")
@Getter
public class OauthDTO {

    @Schema(description = "이메일", example = "codenear@naver.com (APPLE 제외)")
    @NotNull(message = "이메일은 필수 입력 항목입니다.")
    private String email;

    @Schema(description = "Oauth 사용자의 고유 ID", example = "123456789")
    @NotNull(message = "Oauth ID는 필수 입력 항목입니다.")
    private String oauthId;

    @Schema(description = "가입 경로", example = "GOOGLE")
    @Enumerated(EnumType.STRING)
    @NotNull(message = "가입 경로가 올바르지 않습니다.")
    private Platform platform;
}
