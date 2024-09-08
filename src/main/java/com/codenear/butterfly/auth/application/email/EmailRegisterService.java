package com.codenear.butterfly.auth.application.email;

import com.codenear.butterfly.auth.domain.dto.AuthRequestDTO;
import com.codenear.butterfly.member.domain.Grade;
import com.codenear.butterfly.member.domain.Member;
import com.codenear.butterfly.member.domain.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class EmailRegisterService {

    private final CustomUserDetailsService userDetailsService;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public void emailRegister(AuthRequestDTO authRequestDTO) {
        if (hasMember(authRequestDTO.getEmail())) {
            throw new RuntimeException();
        }

        Member newMember = register(authRequestDTO);
        memberRepository.save(newMember);
    }

    private boolean hasMember(String email) {
        try {
            userDetailsService.loadUserByUsername(email);
            return true;
        } catch (UsernameNotFoundException e) {
            return false;
        }
    }

    private Member register(AuthRequestDTO requestDTO) {
        return Member.builder()
                .email(requestDTO.getEmail())
                .nickname(requestDTO.getNickname())
                .password(passwordEncoder.encode(requestDTO.getPassword()))
                .point(0)
                .grade(Grade.LEVEL_1)
                .platform(requestDTO.getPlatform())
                .build();
    }
}