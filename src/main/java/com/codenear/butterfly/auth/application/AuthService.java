package com.codenear.butterfly.auth.application;

import com.codenear.butterfly.auth.application.email.EmailLoginService;
import com.codenear.butterfly.auth.application.email.EmailRegisterService;
import com.codenear.butterfly.auth.domain.dto.AuthLoginDTO;
import com.codenear.butterfly.auth.domain.dto.AuthRegisterDTO;
import com.codenear.butterfly.member.domain.Member;
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

    public void handleRegistration(AuthRegisterDTO requestDTO, HttpServletResponse response) {
        emailRegisterService.emailRegister(requestDTO);
        jwtService.processTokens(requestDTO.getEmail(), Platform.CODENEAR.name(), response);
    }

    public void handleLogin(AuthLoginDTO requestDTO, HttpServletResponse response) {
        Member loginUser = emailLoginService.login(requestDTO, requestDTO.getPassword());
        jwtService.processTokens(loginUser.getEmail(), Platform.CODENEAR.name(), response);
    }
}