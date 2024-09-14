package com.codenear.butterfly.auth.application;

import com.codenear.butterfly.auth.application.email.EmailLoginService;
import com.codenear.butterfly.auth.application.email.EmailRegisterService;
import com.codenear.butterfly.auth.application.jwt.JwtService;
import com.codenear.butterfly.auth.domain.dto.AuthRequestDTO;
import com.codenear.butterfly.auth.domain.dto.CustomUserDetails;
import com.codenear.butterfly.auth.exception.AuthException;
import com.codenear.butterfly.global.exception.ErrorCode;
import com.codenear.butterfly.member.domain.Platform;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final EmailRegisterService emailRegisterService;
    private final EmailLoginService emailLoginService;
    private final JwtService jwtService;

    public void handleRegistration(AuthRequestDTO requestDTO) {
        if (!requestDTO.getPlatform().equals(Platform.CODENEAR)) {
            throw new AuthException(ErrorCode.INVALID_PLATFORM, requestDTO.getPlatform());
        }

        emailRegisterService.emailRegister(requestDTO);
    }

    public void handleLogin(AuthRequestDTO requestDTO, HttpServletResponse response) {
        if (!requestDTO.getPlatform().equals(Platform.CODENEAR)) {
            throw new AuthException(ErrorCode.INVALID_PLATFORM, requestDTO.getPlatform());
        }

        CustomUserDetails loginUser = emailLoginService.login(requestDTO.getEmail(), requestDTO.getPassword());
        jwtService.processTokens(loginUser.getEmail(), requestDTO.getPlatform().name(), response);
    }
}