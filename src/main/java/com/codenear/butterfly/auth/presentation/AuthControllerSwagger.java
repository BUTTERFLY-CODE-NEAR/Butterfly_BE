package com.codenear.butterfly.auth.presentation;

import com.codenear.butterfly.auth.domain.dto.AuthRequestDTO;
import com.codenear.butterfly.global.dto.ResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;

@Tag(name = "Auth", description = "**회원가입 / 로그인 API 명세서**")
public interface AuthControllerSwagger {

    @Operation(summary = "CODE NEAR 회원가입", description = "CODE NEAR 플랫폼으로만 가능한 회원가입 API")
    @PostMapping(value = "/register")
    ResponseEntity<ResponseDTO> register(AuthRequestDTO authRequestDTO);

    @Operation(summary = "로그인 API", description = "모든 플랫폼 로그인 API (KAKAO, GOOGLE 회원가입은 해당 API 요청시, 회원가입 / 로그인 동시 처리) Access, Refresh 토큰 발급")
    @PostMapping(value = "/login")
    ResponseEntity<ResponseDTO> login(AuthRequestDTO requestDTO, HttpServletResponse response);

    @Operation(summary = "토큰 재발급 API", description = "Access, Refresh 토큰 재발급 <br> 요청시 **Refresh 쿠키 전달 필수**")
    @ApiResponses({
            @ApiResponse(responseCode = "40100", description = "발급 과정 오류가 발생, 토큰 재발급 요청으로 Access, Refresh 토큰 재발급 진행 필요"),
            @ApiResponse(responseCode = "40101", description = "발급 과정 오류가 발생, Refresh 토큰 이상으로 재로그인 필요")
    })
    @PostMapping("/reissue")
    ResponseEntity<ResponseDTO> reissue(HttpServletRequest request, HttpServletResponse response);
}