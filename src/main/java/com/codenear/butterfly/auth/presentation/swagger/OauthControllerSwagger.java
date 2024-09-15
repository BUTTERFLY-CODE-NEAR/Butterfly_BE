package com.codenear.butterfly.auth.presentation.swagger;

import com.codenear.butterfly.auth.domain.dto.OauthDTO;
import com.codenear.butterfly.global.dto.ResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Oauth", description = "**소셜 로그인 API 명세서**")
public interface OauthControllerSwagger {

    @Operation(summary = "소셜 로그인 API", description = "KAKAO, GOOGLE 등 Oauth2 사용한 유저정보 기반 로그인 API")
    @PostMapping("/login")
    ResponseEntity<ResponseDTO> login(@Valid @RequestBody OauthDTO oauthDTO, HttpServletResponse response);
}
