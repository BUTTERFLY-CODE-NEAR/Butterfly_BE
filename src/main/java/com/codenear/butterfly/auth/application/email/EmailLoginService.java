package com.codenear.butterfly.auth.application.email;

import com.codenear.butterfly.auth.domain.dto.AuthLoginDTO;
import com.codenear.butterfly.auth.exception.AuthException;
import com.codenear.butterfly.global.exception.ErrorCode;
import com.codenear.butterfly.member.domain.Member;
import com.codenear.butterfly.member.domain.Platform;
import com.codenear.butterfly.member.domain.repository.member.MemberRepository;
import com.codenear.butterfly.member.exception.MemberException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailLoginService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public Member login(AuthLoginDTO requestDTO, String password) {
        Member member = memberRepository.findByEmailAndPlatform(requestDTO.getEmail(), Platform.CODENEAR)
                .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND, null));

        if (!passwordEncoder.matches(password, member.getPassword())) {
            throw new AuthException(ErrorCode.INVALID_EMAIL_OR_PASSWORD, null);
        }

        return member;
    }
}