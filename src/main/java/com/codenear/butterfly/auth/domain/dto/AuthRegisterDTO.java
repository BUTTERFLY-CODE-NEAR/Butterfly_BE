package com.codenear.butterfly.auth.domain.dto;

import com.codenear.butterfly.auth.validator.ValidPassword;
import com.codenear.butterfly.member.domain.Platform;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Schema(title = "개인 회원가입 API JSON", description = "개인 회원가입 시 정의되는 JSON 데이터 입니다.")
@Getter
@ValidPassword
public class AuthRegisterDTO {

    @Schema(description = "회원가입 이메일", example = "codenear@naver.com")
    @Pattern(regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$", message = "올바른 이메일 형식이 아닙니다.")
    @NotNull
    private String email;

    @Schema(description = "회원가입 비밀번호", example = "password")
    @NotNull
    private String password;

    @Schema(description = "닉네임", example = "codenear")
    @Size(min = 2, max = 8, message = "닉네임은 2자 ~ 8자 사이여야 합니다.")
    @NotNull
    private String nickname;

    @Schema(description = "가입 경로 종류", examples = {"GOOGLE", "KAKAO", "CODENEAR"})
    @Enumerated(EnumType.STRING)
    @NotNull(message = "가입 경로가 올바르지 않습니다.")
    private Platform platform;
}
