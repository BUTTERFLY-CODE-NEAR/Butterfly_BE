package com.codenear.butterfly.auth.domain.dto;

import com.codenear.butterfly.member.domain.Platform;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Schema(title = "회원가입/로그인 API JSON", description = "회원가입 및 로그인할 때 정의되는 JSON 데이터 입니다.")
@Getter
public class AuthRequestDTO {

    @Schema(description = "회원가입 및 로그인 이메일", example = "codenear@naver.com")
    @Pattern(regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$", message = "올바른 이메일 형식이 아닙니다.")
    @NotNull
    private String email;

    // todo : 비밀번호 검증 로직 필요 (플랫폼마다 다른 검증 로직 적용 해야함)
    @Schema(description = "회원가입 및 로그인 비밀번호 (GOOGLE, KAKAO 경로는 미사용)", example = "password")
    private String password;

    @Schema(description = "닉네임", example = "codenear")
    @NotNull
    private String nickname;

    // todo : 플랫폼도 검증 로직 필요 (지금은 직렬화 과정에서 발생하는 오류를 핸들러로 대책)
    @Schema(description = "가입 경로 종류", examples = {"GOOGLE", "KAKAO", "CODENEAR"})
    @Enumerated(EnumType.STRING)
    private Platform platform;
}
