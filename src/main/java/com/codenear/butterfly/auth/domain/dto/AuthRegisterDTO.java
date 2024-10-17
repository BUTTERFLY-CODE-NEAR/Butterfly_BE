package com.codenear.butterfly.auth.domain.dto;

import com.codenear.butterfly.auth.annotation.ValidPassword;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Schema(title = "개인 회원가입 JSON", description = "개인 로그인 시 정의되는 JSON 데이터 입니다.")
@Getter
@ValidPassword
public class AuthRegisterDTO {

    @Schema(description = "회원가입 이메일", example = "codenear@naver.com")
    @Pattern(regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$", message = "올바른 이메일 형식이 아닙니다.")
    @NotNull
    private String email;

    @Schema(description = "회원가입 비밀번호", example = "password12!")
    @NotNull
    private String password;

    @Schema(description = "닉네임", example = "codenear")
    @Size(min = 2, max = 8, message = "닉네임은 2자 ~ 8자 사이여야 합니다.")
    @NotNull
    private String nickname;

    @Schema(description = "마케팅 동의", example = "true")
    @NotNull
    private boolean marketingAgreed;
}
