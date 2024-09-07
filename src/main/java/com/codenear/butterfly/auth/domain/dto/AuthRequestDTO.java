package com.codenear.butterfly.auth.domain.dto;

import com.codenear.butterfly.member.domain.Platform;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
@Schema(title = "회원가입/로그인 API JSON", description = "회원가입 및 로그인할 때 정의되는 JSON 데이터 입니다.")
public class AuthRequestDTO {

    @NotNull
    @Schema(description = "회원가입 및 로그인 이메일", example = "codenear@naver.com")
    private String email;

    @Schema(description = "회원가입 및 로그인 비밀번호 (GOOGLE, KAKAO 플랫폼에는 미사용)", example = "password")
    private String password;

    @Schema(description = "닉네임 (CODE NEAR 플랫폼 회원가입 전달 시, 필수 데이터", example = "codenear")
    private String nickname;

    @NotNull
    @Schema(description = "플랫폼 종류", examples = {"GOOGLE", "KAKAO", "CODENEAR"})
    private Platform platform;
}
