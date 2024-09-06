package com.codenear.butterfly.auth.presentation;

import com.codenear.butterfly.auth.application.AuthService;
import com.codenear.butterfly.auth.application.MessageService;
import com.codenear.butterfly.auth.application.email.EmailRegisterService;
import com.codenear.butterfly.auth.application.jwt.JwtService;
import com.codenear.butterfly.auth.domain.dto.AuthRequestDTO;
import com.codenear.butterfly.member.domain.Member;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final JwtService jwtService;
    private final EmailRegisterService emailRegisterService;
    private final MessageService messageService;

    @PostMapping("/auth/register")
    public ResponseEntity<String> register(@RequestBody AuthRequestDTO authRequestDTO) {
        try {
            Member member = emailRegisterService.emailRegister(authRequestDTO);
            log.info(messageService.getMessage("success.register", authRequestDTO.getEmail()));
            return ResponseEntity.status(HttpStatus.CREATED).body("회원가입이 성공적으로 완료되었습니다. 사용자 ID: " + member.getId());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(messageService.getMessage("error.register"));
        }
    }

    @PostMapping("/auth/login")
    @ResponseStatus(HttpStatus.OK)
    public void login(@RequestBody AuthRequestDTO requestDTO, HttpServletResponse response) {
        authService.registerOrLogin(requestDTO);
        jwtService.processTokens(requestDTO.getEmail(), requestDTO.getPlatform().name(), response);
    }

    @PostMapping("/reissue")
    public ResponseEntity<?> reissue(HttpServletRequest request, HttpServletResponse response) {
        try {
            jwtService.processReissue(request, response);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (ResponseStatusException e) {
            return new ResponseEntity<>(e.getMessage(), e.getStatusCode());
        }
    }
}
