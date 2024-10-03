package com.codenear.butterfly.auth.application;

import com.codenear.butterfly.auth.application.email.EmailLoginService;
import com.codenear.butterfly.auth.application.email.EmailRegisterService;
import com.codenear.butterfly.auth.domain.dto.AuthLoginDTO;
import com.codenear.butterfly.auth.domain.dto.AuthRegisterDTO;
import com.codenear.butterfly.member.domain.Member;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final EmailRegisterService emailRegisterService;
    private final EmailLoginService emailLoginService;
    private final JwtService jwtService;

    public void handleRegistration(AuthRegisterDTO requestDTO, HttpServletResponse response) {
        Member member = emailRegisterService.emailRegister(requestDTO);
        jwtService.processTokens(member.getId(), response);
    }

    public void handleLogin(AuthLoginDTO requestDTO, HttpServletResponse response) {
        Member member = emailLoginService.login(requestDTO, requestDTO.getPassword());
        jwtService.processTokens(member.getId(), response);
    }
}