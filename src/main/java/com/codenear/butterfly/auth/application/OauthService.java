package com.codenear.butterfly.auth.application;

import com.codenear.butterfly.auth.domain.dto.OauthDTO;
import com.codenear.butterfly.member.application.NicknameService;
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
    private final NicknameService nicknameService;

    public void socialLoginAndIssueJwt(OauthDTO dto, HttpServletResponse response) {
        registerOrLogin(dto);
        jwtService.processTokens(dto.getEmail(), dto.getPlatform().name(), response);
    }

    private void registerOrLogin(OauthDTO dto) {
        Optional<Member> optMember = memberRepository.findByEmailAndPassword(dto.getEmail(), dto.getOauthId());

        if (optMember.isEmpty()) {
            Member member = createMember(dto);
            memberRepository.save(member);
        }
    }

    private Member createMember(OauthDTO dto) {
        return Member.builder()
                .email(dto.getEmail())
                .password(dto.getOauthId())
                .nickname(nicknameService.generateNickname())
                .point(0)
                .grade(Grade.EGG)
                .platform(dto.getPlatform())
                .build();
    }
}
