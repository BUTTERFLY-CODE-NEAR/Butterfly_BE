package com.codenear.butterfly.member.application;

import com.codenear.butterfly.certify.application.CertifyService;
import com.codenear.butterfly.certify.domain.CertifyType;
import com.codenear.butterfly.certify.domain.dto.CertifyRequest;
import com.codenear.butterfly.global.exception.ErrorCode;
import com.codenear.butterfly.member.domain.Member;
import com.codenear.butterfly.member.domain.dto.FindPasswordRequestDTO;
import com.codenear.butterfly.member.domain.dto.ResetPasswordRequestDTO;
import com.codenear.butterfly.member.domain.dto.VerifyFindPasswordRequestDTO;
import com.codenear.butterfly.member.domain.repository.member.MemberRepository;
import com.codenear.butterfly.member.exception.MemberException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CredentialService {
    private final MemberRepository memberRepository;
    private final CertifyService certifyService;
    private final PasswordEncoder passwordEncoder;

    public void sendFindEmailCode(String phoneNumber) {
        validateMemberExistsByPhone(phoneNumber);
        certifyService.sendCertifyCode(phoneNumber, CertifyType.CERTIFY_PHONE);
    }

    public String findEmail(CertifyRequest request) {
        certifyService.checkCertifyCode(request, CertifyType.CERTIFY_PHONE);
        Member member = loadMemberByPhoneNumber(request.phoneNumber());
        return member.getEmail();
    }

    public void sendFindPasswordCode(FindPasswordRequestDTO request) {
        switch (request.getType()) {
            case PHONE -> {
                validateMemberExistsByPhone(request.getIdentifier());
                certifyService.sendCertifyCode(request.getIdentifier(), CertifyType.CERTIFY_PHONE);
            }
            case EMAIL -> {
                validateMemberExistsByEmail(request.getIdentifier());
                certifyService.sendCertifyCode(request.getIdentifier(), CertifyType.CERTIFY_EMAIL);
            }
        }
    }

    public void verifyFindPasswordCode(VerifyFindPasswordRequestDTO request) {
        switch (request.getType()) {
            case PHONE -> {
                CertifyRequest certifyRequest =
                        new CertifyRequest(request.getIdentifier(), null, request.getCertifyCode());
                certifyService.checkCertifyCode(certifyRequest, CertifyType.CERTIFY_PHONE);
            }
            case EMAIL -> {
                CertifyRequest certifyRequest =
                        new CertifyRequest(null, request.getIdentifier(), request.getCertifyCode());
                certifyService.checkCertifyCode(certifyRequest, CertifyType.CERTIFY_EMAIL);
            }
        };
    }

    @CacheEvict(value = "userCache, memberCache", key = "#memberId")
    public void resetPassword(ResetPasswordRequestDTO request) {
        Member member = switch (request.getType()) {
            case PHONE -> loadMemberByPhoneNumber(request.getIdentifier());
            case EMAIL -> loadMemberByEmail(request.getIdentifier());
        };
        
        String encodedPassword = passwordEncoder.encode(request.getNewPassword());
        member.updatePassword(encodedPassword);

        memberRepository.save(member);
    }

    private void validateMemberExistsByPhone(String phoneNumber) {
        if (!memberRepository.findByPhoneNumber(phoneNumber).isPresent()) {
            throw new MemberException(ErrorCode.MEMBER_NOT_FOUND_BY_PHONE, null);
        }
    }

    private void validateMemberExistsByEmail(String email) {
        if (!memberRepository.findByEmail(email).isPresent()) {
            throw new MemberException(ErrorCode.MEMBER_NOT_FOUND_BY_EMAIL, null);
        }
    }

    private Member loadMemberByPhoneNumber(String phoneNumber) {
        return memberRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND_BY_PHONE, null));
    }

    private Member loadMemberByEmail(String email) {
        return memberRepository.findByPhoneNumber(email)
                .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND_BY_EMAIL, null));
    }
}
