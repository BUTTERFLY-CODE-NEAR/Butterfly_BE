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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Optional;

@Service
@Transactional
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final ErrorMessageService errorMessageService;

    public AuthService(AuthenticationManager authenticationManager, CustomUserDetailsService userDetailsService, MemberRepository memberRepository, PasswordEncoder passwordEncoder, ErrorMessageService errorMessageService) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.memberRepository = memberRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
        this.errorMessageService = errorMessageService;
    }

    public void registerOrLogin(AuthRequestDTO requestDTO) {
        Optional<Member> member = memberRepository.findByEmailAndPlatform(requestDTO.getEmail(), requestDTO.getPlatform());

        if (member.isEmpty()) { // 회원 정보 DB 저장
            Member registerMember = register(requestDTO);
            memberRepository.save(registerMember);
        }
    }

    public CustomUserDetails authenticate(AuthRequestDTO authRequestDTO) {
        try {
            // userDetailsService 를 이용해 사용자 정보 로드
            UserDetails userDetails = userDetailsService.loadUserByUsername(authRequestDTO.getEmail());

            // 인증 토큰 생성
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(userDetails, authRequestDTO.getPassword(), userDetails.getAuthorities());

            // 인증 시도 및 컨텍스트에 저장
            Authentication authentication = authenticationManager.authenticate(authenticationToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            return (CustomUserDetails) authentication.getPrincipal();
        } catch (AuthenticationException e) {
            throw new RuntimeException(errorMessageService.getErrorMessage("error.invalidCredentials"), e);
        }
    }

    public Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException(errorMessageService.getErrorMessage("error.noAuthenticatedUser"));
        }

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        return userDetails.getId();
    }

    public Member emailRegister(AuthRequestDTO authRequestDTO) {
        if (hasMember(authRequestDTO)) {
            throw new RuntimeException(errorMessageService.getErrorMessage("error.emailAlreadyInUse"));
        }

        Member newMember = register(authRequestDTO);
        return memberRepository.save(newMember);
    }

    private boolean hasMember(AuthRequestDTO authRequestDTO) {
        return memberRepository.findByEmail(authRequestDTO.getEmail()).isPresent();
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