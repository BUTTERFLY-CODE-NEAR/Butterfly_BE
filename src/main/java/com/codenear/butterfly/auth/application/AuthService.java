package com.codenear.butterfly.auth.application;

import com.codenear.butterfly.auth.application.email.EmailLoginService;
import com.codenear.butterfly.auth.application.email.EmailRegisterService;
import com.codenear.butterfly.auth.application.jwt.JwtService;
import com.codenear.butterfly.auth.domain.dto.AuthRequestDTO;
import com.codenear.butterfly.auth.domain.dto.CustomUserDetails;
import com.codenear.butterfly.member.domain.Grade;
import com.codenear.butterfly.member.domain.Member;
import com.codenear.butterfly.member.domain.Platform;
import com.codenear.butterfly.member.domain.repository.MemberRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final MemberRepository memberRepository;
    private final EmailRegisterService emailRegisterService;
    private final EmailLoginService emailLoginService;
    private final JwtService jwtService;
    private final MessageService messageService;

    public void handleRegistration(AuthRequestDTO requestDTO) {
        log.info(messageService.getMessage("log.registerRequest", requestDTO.getEmail()));

        try {
            Map<Platform, Runnable> platformActions = Map.of(
                    Platform.CODENEAR, () -> emailRegisterService.emailRegister(requestDTO),
                    Platform.KAKAO, () -> registerOrLogin(requestDTO),
                    Platform.GOOGLE, () -> registerOrLogin(requestDTO)
            );
            Optional.ofNullable(platformActions.get(requestDTO.getPlatform()))
                    .orElseThrow(() -> new IllegalArgumentException("제공하지 않는 플랫폼입니다."))
                    .run();
            log.info(messageService.getMessage("log.registerSuccess", requestDTO.getEmail()));
        } catch (RuntimeException e) {
            log.error(messageService.getMessage("error.emailAlreadyInUse"));
            throw e;
        }
    }

    public void handleLogin(AuthRequestDTO requestDTO, HttpServletResponse response) {
        log.info(messageService.getMessage("log.loginRequest", requestDTO.getEmail(), requestDTO.getPlatform()));

        try {
            Map<Platform, Runnable> loginActions = Map.of(
                    Platform.CODENEAR, () -> emailLoginAndIssueJwt(requestDTO, response),
                    Platform.KAKAO, () -> socialLoginAndIssueJwt(requestDTO, response),
                    Platform.GOOGLE, () -> socialLoginAndIssueJwt(requestDTO, response)
            );

            Optional.ofNullable(loginActions.get(requestDTO.getPlatform()))
                    .orElseThrow(() -> new IllegalArgumentException("제공하지 않는 플랫폼입니다."))
                    .run();

            log.info(messageService.getMessage("log.loginSuccess", requestDTO.getEmail()));
        } catch (BadCredentialsException e) {
            log.error(e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error(messageService.getMessage("error.internalServerError"));
            throw e;
        }
    }

    private void emailLoginAndIssueJwt(AuthRequestDTO requestDTO, HttpServletResponse response) {
        CustomUserDetails loginUser = emailLoginService.login(requestDTO.getEmail(), requestDTO.getPassword());
        issueJwtTokens(loginUser.getEmail(), requestDTO.getPlatform().name(), response);
    }

    private void socialLoginAndIssueJwt(AuthRequestDTO requestDTO, HttpServletResponse response) {
        registerOrLogin(requestDTO);
        issueJwtTokens(requestDTO.getEmail(), requestDTO.getPlatform().name(), response);
    }

    private void issueJwtTokens(String email, String platform, HttpServletResponse response) {
        jwtService.processTokens(email, platform, response);
        log.info(messageService.getMessage("log.jwtCreated", email));
    }

    public void registerOrLogin(AuthRequestDTO requestDTO) {
        Optional<Member> optMember = memberRepository.findByEmailAndPlatform(requestDTO.getEmail(), requestDTO.getPlatform());

        if (optMember.isEmpty()) {
            Member registerMember = register(requestDTO);
            memberRepository.save(registerMember);
        }
    }

    private Member register(AuthRequestDTO requestDTO) {
        return Member.builder()
                .email(requestDTO.getEmail())
                .nickname(requestDTO.getNickname())
                .point(0)
                .grade(Grade.LEVEL_1)
                .platform(requestDTO.getPlatform())
                .build();
    }
}