package com.codenear.butterfly.auth.application;

import com.codenear.butterfly.auth.domain.dto.AuthRequestDTO;
import com.codenear.butterfly.member.domain.Grade;
import com.codenear.butterfly.member.domain.Member;
import com.codenear.butterfly.member.domain.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final MemberRepository memberRepository;

    public Member registerOrLogin(AuthRequestDTO requestDTO) {
        Optional<Member> OptMember = memberRepository.findByEmailAndPlatform(requestDTO.getEmail(), requestDTO.getPlatform());
        Member member;

        if (OptMember.isEmpty()) { // 회원 정보 DB 저장
            Member registerMember = register(requestDTO);
            member = memberRepository.save(registerMember);
        } else {
            member = OptMember.get();
        }

        return member;
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
