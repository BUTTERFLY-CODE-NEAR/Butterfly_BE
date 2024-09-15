package com.codenear.butterfly.auth.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Schema(title = "개인 로그인 API JSON", description = "개인 로그인 시 정의되는 JSON 데이터 입니다.")
@Getter
public class AuthLoginDTO {

    @Schema(description = "로그인 이메일", example = "codenear@naver.com")
    @Pattern(regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$", message = "올바른 이메일 형식이 아닙니다.")
    @NotNull(message = "이메일을 입력해주세요.")
    private String email;

    @Schema(description = "로그인 비밀번호", example = "password")
    @NotNull(message = "비밀번호를 입력해주세요.")
    private String password;

}
