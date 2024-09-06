package com.codenear.butterfly.auth.application.email;

import com.codenear.butterfly.auth.application.MessageService;
import com.codenear.butterfly.auth.domain.dto.AuthRequestDTO;
import com.codenear.butterfly.member.domain.Grade;
import com.codenear.butterfly.member.domain.Member;
import com.codenear.butterfly.member.domain.Role;
import com.codenear.butterfly.member.domain.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class EmailRegisterService {

    private final CustomUserDetailsService userDetailsService;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final MessageService messageService;

    public Member emailRegister(AuthRequestDTO authRequestDTO) {
        if (hasMember(authRequestDTO)) {
            String errorMessage = messageService.getMessage("error.emailAlreadyInUse");
            log.info(errorMessage);
            throw new RuntimeException(errorMessage);
        }

        Member newMember = register(authRequestDTO);
        return memberRepository.save(newMember);
    }

    private boolean hasMember(AuthRequestDTO authRequestDTO) {
        try {
            userDetailsService.loadUserByUsername(authRequestDTO.getEmail());
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
                .roles(Collections.singleton(Role.USER))
                .build();
    }
}