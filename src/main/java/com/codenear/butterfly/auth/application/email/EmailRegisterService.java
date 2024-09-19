package com.codenear.butterfly.auth.application.email;

import com.codenear.butterfly.auth.domain.dto.AuthRegisterDTO;
import com.codenear.butterfly.auth.exception.AuthException;
import com.codenear.butterfly.global.exception.ErrorCode;
import com.codenear.butterfly.member.domain.Grade;
import com.codenear.butterfly.member.domain.Member;
import com.codenear.butterfly.member.domain.Platform;
import com.codenear.butterfly.member.domain.repository.member.MemberRepository;
import com.codenear.butterfly.member.util.ForbiddenWordFilter;
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
    private final ForbiddenWordFilter forbiddenWordFilter;

    public void emailRegister(AuthRegisterDTO authRegisterDTO) {
        if (hasMember(authRegisterDTO.getEmail())) {
            throw new AuthException(ErrorCode.EMAIL_ALREADY_IN_USE, authRegisterDTO.getEmail());
        }

        validateNickname(authRegisterDTO.getNickname());

        Member newMember = register(authRegisterDTO);
        memberRepository.save(newMember);
    }

    private void validateNickname(String nickname) {
        if (forbiddenWordFilter.containsForbiddenWord(nickname)) {
            throw new AuthException(ErrorCode.FORBIDDEN_NICKNAME, nickname);
        }
    }

    private boolean hasMember(String email) {
        try {
            userDetailsService.loadUserByUsername(email);
            return true;
        } catch (UsernameNotFoundException e) {
            return false;
        }
    }

    private Member register(AuthRegisterDTO requestDTO) {
        return Member.createMemberWithPoint(
                requestDTO.getEmail(),
                passwordEncoder.encode(requestDTO.getPassword()),
                requestDTO.getNickname(),
                Grade.EGG,
                Platform.CODENEAR
        );
    }
}