package com.codenear.butterfly.auth.presentation;

import com.codenear.butterfly.auth.domain.dto.OAuthRequestDTO;
import com.codenear.butterfly.auth.application.OAuthService;
import com.codenear.butterfly.auth.jwt.JwtUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@ResponseBody
public class OAuthController {
    private final JwtUtil jwtUtil;
    private final OAuthService authService;

    @PostMapping("/auth/login")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void login(@RequestBody OAuthRequestDTO authRequestDTO, HttpServletResponse response) {
        authService.registerOrLogin(authRequestDTO);
        String token = jwtUtil.createJwt(authRequestDTO);
        response.addHeader("Authorization", "Bearer " + token);
    }
}
