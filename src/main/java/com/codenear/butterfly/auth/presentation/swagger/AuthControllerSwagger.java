package com.codenear.butterfly.auth.presentation.swagger;

import com.codenear.butterfly.auth.domain.dto.AuthLoginDTO;
import com.codenear.butterfly.auth.domain.dto.AuthRegisterDTO;
import com.codenear.butterfly.global.dto.ResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;

@Tag(name = "Auth", description = "**개인(CODE NEAR) 회원가입 / 로그인 API**")
public interface AuthControllerSwagger {

    @Operation(summary = "개인(CODE NEAR) 회원가입", description = "개인(CODE NEAR) 플랫폼으로만 가능한 회원가입 API")
    @ApiResponses({
            @ApiResponse(responseCode = "40001", description = "닉네임 생성 중 오류 발생"),
            @ApiResponse(responseCode = "40002", description = "잘못된 닉네임 형식"),
            @ApiResponse(responseCode = "40003", description = "닉네임에 금지어 포함"),
            @ApiResponse(responseCode = "40900", description = "이메일 중복"),
    })
    ResponseEntity<ResponseDTO> register(AuthRegisterDTO requestDTO, HttpServletResponse response);

    @Operation(summary = "개인(CODE NEAR) 로그인", description = "개인(CODE NEAR) 로그인 API **(Access, Refresh 토큰 발급)**")
    @ApiResponses({
            @ApiResponse(responseCode = "40300", description = "아이디 혹은 비밀번호가 불일치"),
    })
    ResponseEntity<ResponseDTO> login(AuthLoginDTO requestDTO, HttpServletResponse response);

    @Operation(summary = "토큰 재발급", description = "Access, Refresh 토큰 재발급 API **(Refresh 쿠키 필요)**")
    @ApiResponses({
            @ApiResponse(responseCode = "40100", description = "발급 과정 오류가 발생, 토큰 재발급 요청으로 Access, Refresh 토큰 재발급 진행 필요"),
            @ApiResponse(responseCode = "40101", description = "발급 과정 오류가 발생, Refresh 토큰 이상으로 재로그인 필요")
    })
    ResponseEntity<ResponseDTO> reissue(HttpServletRequest request, HttpServletResponse response);
}