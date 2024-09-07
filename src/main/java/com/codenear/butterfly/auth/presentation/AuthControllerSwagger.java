package com.codenear.butterfly.auth.presentation;

import com.codenear.butterfly.auth.domain.dto.AuthRequestDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;

@Tag(name = "Auth", description = "회원가입 / 로그인 API 명세서")
public interface AuthControllerSwagger {

    @Operation(summary = "CODE NEAR 회원가입", description = "CODE NEAR 플랫폼으로만 가능한 회원가입 API")
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    ResponseEntity<String> register(@RequestBody AuthRequestDTO authRequestDTO);

    @Operation(summary = "로그인 API", description = "모든 플랫폼 로그인 API (KAKAO, GOOGLE 회원가입은 해당 API 요청시, 회원가입 / 로그인 동시 처리) Access, Refresh 토큰 발급")
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    @ResponseStatus
    ResponseEntity<?> login(@RequestBody AuthRequestDTO requestDTO, HttpServletResponse response);

    @Operation(summary = "토큰 재발급 API", description = "Access, Refresh 토큰 재발급 API (Refresh 쿠키 전달 필요)")
    @RequestMapping(value = "/reissue", method = RequestMethod.POST)
    ResponseEntity<?> reissue(HttpServletRequest request, HttpServletResponse response);
}