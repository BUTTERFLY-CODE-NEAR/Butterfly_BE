package com.codenear.butterfly.auth.presentation;

import com.codenear.butterfly.auth.application.ErrorMessageService;
import com.codenear.butterfly.auth.domain.dto.AuthRequestDTO;
import com.codenear.butterfly.auth.application.AuthService;
import com.codenear.butterfly.member.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final ErrorMessageService errorMessageService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody AuthRequestDTO authRequestDTO) {
        try {
            Member member = authService.emailRegister(authRequestDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body("회원가입이 성공적으로 완료되었습니다. 사용자 ID: " + member.getId());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessageService.getErrorMessage("error.register"));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Void> login(@RequestBody AuthRequestDTO authRequestDTO) {
        authService.registerOrLogin(authRequestDTO);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
