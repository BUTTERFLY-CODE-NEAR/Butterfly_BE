package com.codenear.butterfly.auth.application;

import com.codenear.butterfly.auth.application.jwt.JwtService;
import com.codenear.butterfly.auth.domain.dto.AuthRequestDTO;
import com.codenear.butterfly.member.domain.Grade;
import com.codenear.butterfly.member.domain.Member;
import com.codenear.butterfly.member.domain.repository.member.MemberRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OauthService {
    private final MemberRepository memberRepository;
    private final JwtService jwtService;

    public void socialLoginAndIssueJwt(AuthRequestDTO requestDTO, HttpServletResponse response) {
        registerOrLogin(requestDTO);
        jwtService.processTokens(requestDTO.getEmail(), requestDTO.getPlatform().name(), response);
    }

    private void registerOrLogin(AuthRequestDTO requestDTO) {
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
                .grade(Grade.EGG)
                .platform(requestDTO.getPlatform())
                .build();
    }
}
