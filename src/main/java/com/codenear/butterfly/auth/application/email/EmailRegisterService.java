package com.codenear.butterfly.auth.application.email;

import com.codenear.butterfly.auth.domain.dto.AuthRegisterDTO;
import com.codenear.butterfly.auth.exception.AuthException;
import com.codenear.butterfly.consent.application.ConsentFacade;
import com.codenear.butterfly.consent.domain.ConsentType;
import com.codenear.butterfly.global.exception.ErrorCode;
import com.codenear.butterfly.member.domain.Grade;
import com.codenear.butterfly.member.domain.Member;
import com.codenear.butterfly.member.domain.Platform;
import com.codenear.butterfly.member.domain.repository.member.DeletedMemberRepository;
import com.codenear.butterfly.member.domain.repository.member.MemberRepository;
import com.codenear.butterfly.member.infrastructure.MemberDataAccess;
import com.codenear.butterfly.member.util.ForbiddenWordFilter;
import com.codenear.butterfly.point.domain.Point;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class EmailRegisterService {
    private final MemberRepository memberRepository;
    private final MemberDataAccess memberDataAccess;
    private final ConsentFacade consentFacade;
    private final PasswordEncoder passwordEncoder;
    private final ForbiddenWordFilter forbiddenWordFilter;
    private final DeletedMemberRepository deletedMemberRepository;

    public Member emailRegister(AuthRegisterDTO authRegisterDTO) {
        String email = authRegisterDTO.getEmail();
        if (hasMember(email)) {
            if (isWithdrawn(email)) {
                deletedMemberRepository.deleteByMember_Email(email);
                return memberDataAccess.save(restoreId(email));
            }
            throw new AuthException(ErrorCode.EMAIL_ALREADY_IN_USE, authRegisterDTO.getEmail());
        }

        validateNickname(authRegisterDTO.getNickname());

        Member newMember = register(authRegisterDTO);

        consentFacade.saveConsent(ConsentType.MARKETING, authRegisterDTO.isMarketingAgreed(), newMember);
        return memberDataAccess.save(newMember);
    }

    private void validateNickname(String nickname) {
        if (forbiddenWordFilter.containsForbiddenWord(nickname)) {
            throw new AuthException(ErrorCode.FORBIDDEN_NICKNAME, nickname);
        }
    }

    private boolean hasMember(String email) {
        return memberRepository.findByEmailAndPlatform(email, Platform.CODENEAR).isPresent();
    }

    private boolean isWithdrawn(String email) {
        return memberRepository.findByEmailAndPlatform(email, Platform.CODENEAR).get().isDeleted();
    }

    private Member restoreId(String email){
        Member member = memberRepository.findByEmailAndPlatform(email, Platform.CODENEAR).get();
        member.restore();
        return member;
    }

    private Member register(AuthRegisterDTO requestDTO) {
        Member member = Member.builder()
                .email(requestDTO.getEmail())
                .password(passwordEncoder.encode(requestDTO.getPassword()))
                .nickname(requestDTO.getNickname())
                .grade(Grade.EGG)
                .platform(Platform.CODENEAR)
                .build();

        Point point = Point.createPoint()
                .member(member)
                .build();

        member.setPoint(point);

        return member;
    }
}