package com.codenear.butterfly.auth.presentation;

import com.codenear.butterfly.auth.application.AuthService;
import com.codenear.butterfly.auth.application.EmailRegisterService;
import com.codenear.butterfly.auth.application.ErrorMessageService;
import com.codenear.butterfly.auth.domain.dto.AuthRequestDTO;
import com.codenear.butterfly.auth.jwt.JwtUtil;
import com.codenear.butterfly.member.domain.Member;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@ResponseBody
public class AuthController {
    private final JwtUtil jwtUtil;
    private final AuthService authService;
    private final EmailRegisterService emailRegisterService;
    private final ErrorMessageService errorMessageService;

    @PostMapping("/auth/register")
    public ResponseEntity<String> register(@RequestBody AuthRequestDTO authRequestDTO) {
        try {
            Member member = emailRegisterService.emailRegister(authRequestDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body("회원가입이 성공적으로 완료되었습니다. 사용자 ID: " + member.getId());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessageService.getErrorMessage("error.register"));
        }
    }

    @PostMapping("/auth/login")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void login(@RequestBody AuthRequestDTO authRequestDTO, HttpServletResponse response) {
        authService.registerOrLogin(authRequestDTO);
        String token = jwtUtil.createJwt(authRequestDTO);
        response.addHeader("Authorization", "Bearer " + token);
    }
}
