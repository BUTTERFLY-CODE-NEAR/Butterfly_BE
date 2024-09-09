package com.codenear.butterfly.auth.presentation;

import com.codenear.butterfly.auth.application.AuthService;
import com.codenear.butterfly.auth.exception.message.MessageUtil;
import com.codenear.butterfly.auth.application.jwt.JwtService;
import com.codenear.butterfly.auth.domain.dto.AuthRequestDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController implements AuthControllerSwagger {
    private final AuthService authService;
    private final JwtService jwtService;
    private final MessageUtil messageUtil;

    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody AuthRequestDTO requestDTO) {
        authService.handleRegistration(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(messageUtil.getMessage("success.register", requestDTO.getEmail()));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody AuthRequestDTO requestDTO, HttpServletResponse response) {
        authService.handleLogin(requestDTO, response);
        return ResponseEntity.ok(messageUtil.getMessage("log.loginSuccess", requestDTO.getEmail()));
    }

    @PostMapping("/reissue")
    public ResponseEntity<?> reissue(HttpServletRequest request, HttpServletResponse response) {
        jwtService.processReissue(request, response);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}