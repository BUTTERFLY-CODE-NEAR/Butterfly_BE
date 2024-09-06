package com.codenear.butterfly.auth.application;

import com.codenear.butterfly.auth.domain.dto.AuthRequestDTO;
import com.codenear.butterfly.auth.domain.dto.CustomUserDetails;
import com.codenear.butterfly.member.domain.Grade;
import com.codenear.butterfly.member.domain.Member;
import com.codenear.butterfly.member.domain.Role;
import com.codenear.butterfly.member.domain.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Optional;

@Service
@Transactional
public class EmailRegisterService {

    private final CustomUserDetailsService userDetailsService;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final ErrorMessageService errorMessageService;

    public EmailRegisterService(CustomUserDetailsService userDetailsService, MemberRepository memberRepository, PasswordEncoder passwordEncoder, ErrorMessageService errorMessageService) {
        this.userDetailsService = userDetailsService;
        this.memberRepository = memberRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
        this.errorMessageService = errorMessageService;
    }

    public Member emailRegister(AuthRequestDTO authRequestDTO) {
        if (hasMember(authRequestDTO)) {
            throw new RuntimeException(errorMessageService.getErrorMessage("error.emailAlreadyInUse"));
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