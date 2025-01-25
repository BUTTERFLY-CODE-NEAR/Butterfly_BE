package com.codenear.butterfly.global.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class PasswordDTO {
    @Schema(description = "회원가입/재설정 비밀번호", example = "password12!")
    @NotNull
    private String password;

    public PasswordDTO(String password) {
    }
}
