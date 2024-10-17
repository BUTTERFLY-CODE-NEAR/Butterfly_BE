package com.codenear.butterfly.auth.presentation.swagger;

import com.codenear.butterfly.global.dto.ResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(name = "Auth", description = "**개인(CODE NEAR) 회원가입 / 로그인 API**")
public interface LogoutControllerSwagger {

    @Operation(summary = "개인(CODE NEAR), 소셜 로그아웃", description = "모든 경로의 로그아웃 API")
    ResponseEntity<ResponseDTO> logout();
}
