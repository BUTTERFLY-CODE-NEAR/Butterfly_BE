package com.codenear.butterfly.auth.presentation;

import com.codenear.butterfly.auth.application.AuthService;
import com.codenear.butterfly.auth.domain.dto.AuthRequestDTO;
import com.codenear.butterfly.auth.jwt.JwtUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@ResponseBody
public class AuthController {
    private final JwtUtil jwtUtil;
    private final AuthService authService;

    @PostMapping("/auth/login")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void login(@RequestBody AuthRequestDTO authRequestDTO, HttpServletResponse response) {
        authService.registerOrLogin(authRequestDTO);
        String token = jwtUtil.createJwt(authRequestDTO);
        response.addHeader("Authorization", "Bearer " + token);
    }
}
