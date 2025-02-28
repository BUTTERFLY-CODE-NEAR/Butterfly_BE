package com.codenear.butterfly.auth.presentation;

import com.codenear.butterfly.auth.application.AuthService;
import com.codenear.butterfly.auth.application.JwtService;
import com.codenear.butterfly.auth.domain.dto.AuthLoginDTO;
import com.codenear.butterfly.auth.domain.dto.AuthRegisterDTO;
import com.codenear.butterfly.auth.presentation.swagger.AuthControllerSwagger;
import com.codenear.butterfly.global.dto.ResponseDTO;
import com.codenear.butterfly.global.util.MessageUtil;
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

import static com.codenear.butterfly.global.util.ResponseUtil.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController implements AuthControllerSwagger {
    private final AuthService authService;
    private final JwtService jwtService;
    private final MessageUtil messageUtil;

    @PostMapping("/register")
    public ResponseEntity<ResponseDTO> register(@Valid @RequestBody AuthRegisterDTO requestDTO, HttpServletResponse response) {
        authService.handleRegistration(requestDTO, response);
        return createSuccessResponse(HttpStatus.CREATED, messageUtil.getMessage("success.register", requestDTO.getEmail()), null);
    }

    @PostMapping("/login")
    public ResponseEntity<ResponseDTO> login(@Valid @RequestBody AuthLoginDTO requestDTO, HttpServletResponse response) {
        authService.handleLogin(requestDTO, response);
        return createSuccessResponse(messageUtil.getMessage("log.loginSuccess", requestDTO.getEmail()), null);
    }

    @PostMapping("/reissue")
    public ResponseEntity<ResponseDTO> reissue(HttpServletRequest request, HttpServletResponse response) {
        jwtService.processReissue(request, response);
        return createSuccessResponse(null);
    }
}