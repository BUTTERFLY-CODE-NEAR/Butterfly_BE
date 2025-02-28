package com.codenear.butterfly.auth.application;

import com.codenear.butterfly.auth.domain.dto.OauthDTO;
import com.codenear.butterfly.member.application.NicknameService;
import com.codenear.butterfly.member.domain.Grade;
import com.codenear.butterfly.member.domain.Member;
import com.codenear.butterfly.member.domain.repository.member.MemberRepository;
import com.codenear.butterfly.point.domain.Point;
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
        Member member = registerOrLogin(dto);
        jwtService.processTokens(member.getId(), response);
    }

    private Member registerOrLogin(OauthDTO dto) {
        Optional<Member> optMember = memberRepository.findByEmailAndPlatform(dto.getEmail(), dto.getPlatform());

        if (optMember.isEmpty()) {
            Member member = createMember(dto);
            return memberRepository.save(member);
        }

        return optMember.get();
    }

    private Member createMember(OauthDTO dto) {
        Member member = Member.builder()
                .email(dto.getEmail())
                .password(dto.getOauthId())
                .nickname(nicknameService.generateNickname())
                .grade(Grade.EGG)
                .platform(dto.getPlatform())
                .build();

        Point point = Point.createPoint()
                .member(member)
                .build();

        member.setPoint(point);

        return member;
    }
}
