package com.codenear.butterfly.auth.application;

import com.codenear.butterfly.member.domain.Grade;
import com.codenear.butterfly.member.domain.Member;
import com.codenear.butterfly.member.domain.repository.MemberRepository;
import com.codenear.butterfly.auth.domain.dto.OAuthRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class OAuthService {

    private final MemberRepository memberRepository;

    public void registerOrLogin(OAuthRequestDTO requestDTO) {
        Optional<Member> member = memberRepository.findByEmailAndPlatform(requestDTO.getEmail(), requestDTO.getPlatform());

        if (member.isEmpty()) { // 회원 정보 DB 저장
            Member registerMember = register(requestDTO);
            memberRepository.save(registerMember);
        }
    }

    private Member register(OAuthRequestDTO requestDTO) {
        return Member.builder()
                .email(requestDTO.getEmail())
                .nickname(requestDTO.getNickname())
                .point(0)
                .grade(Grade.LEVEL_1)
                .platform(requestDTO.getPlatform())
                .build();
    }
}
