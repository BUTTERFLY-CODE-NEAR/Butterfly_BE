package com.codenear.butterfly.auth.domain.dto;

import com.codenear.butterfly.member.domain.Platform;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Schema(title = "개인 로그인 JSON", description = "개인 로그인 시 정의되는 JSON 데이터 입니다.")
@Getter
public class AuthLoginDTO {

    @Schema(description = "로그인 이메일", example = "codenear@naver.com")
    @Pattern(regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$", message = "올바른 이메일 형식이 아닙니다.")
    @NotNull(message = "이메일을 입력해주세요.")
    private String email;

    @Schema(description = "로그인 비밀번호", example = "password12!")
    @NotNull(message = "비밀번호를 입력해주세요.")
    private String password;

    @Schema(description = "가입 경로", example = "GOOGLE")
    @Enumerated(EnumType.STRING)
    @NotNull(message = "가입 경로가 올바르지 않습니다.")
    private Platform platform;

}
