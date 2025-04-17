package com.codenear.butterfly.auth.presentation.swagger;

import com.codenear.butterfly.global.dto.ResponseDTO;
import com.codenear.butterfly.member.domain.dto.MemberDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;


@Tag(name = "Auth", description = "**개인(CODE NEAR) 회원가입 / 로그인 API**")
public interface LogoutAndWithdrawControllerSwagger {

    @Operation(summary = "개인(CODE NEAR), 소셜 로그아웃", description = "모든 경로의 로그아웃 API")
    ResponseEntity<ResponseDTO> logout();

    @Operation(summary = "회원탈퇴", description = "회원탈퇴 API")
    ResponseEntity<ResponseDTO> withdraw(@AuthenticationPrincipal MemberDTO loginMember, HttpServletResponse response);
}
