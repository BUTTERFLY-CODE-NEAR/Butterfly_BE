package com.codenear.butterfly.member.application;

import static com.codenear.butterfly.certify.domain.CertifyType.REGISTER_PHONE;
import static com.codenear.butterfly.global.exception.ErrorCode.PHONE_NUMBER_ALREADY_USE;

import com.codenear.butterfly.certify.application.CertifyService;
import com.codenear.butterfly.member.domain.Member;
import com.codenear.butterfly.member.domain.dto.MemberDTO;
import com.codenear.butterfly.member.exception.MemberException;
import com.codenear.butterfly.member.infrastructure.MemberDataAccess;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class PhoneRegistrationService {

    private final CertifyService certifyService;
    private final MemberDataAccess memberDataAccess;

    public void sendRegistrationCode(String phoneNumber, MemberDTO loginMember) {
        validatePhoneNumberDuplicate(phoneNumber);

        certifyService.sendCertifyCode(phoneNumber, REGISTER_PHONE);

        Member member = getMemberById(loginMember.getId());
        updatePhoneNumber(phoneNumber, member);
    }

    private Member getMemberById(Long memberId) {
        return memberDataAccess.findByMemberId(memberId);
    }

    private void updatePhoneNumber(String phoneNumber, Member member) {
        member.setPhoneNumber(phoneNumber);
        memberDataAccess.save(member);
    }

    private void validatePhoneNumberDuplicate(String phoneNumber) {
        memberDataAccess.findByPhoneNumber(phoneNumber)
                .ifPresent(member -> {
                    throw new MemberException(PHONE_NUMBER_ALREADY_USE, phoneNumber);
                });
    }
}
