package com.codenear.butterfly.member.application;

import static com.codenear.butterfly.certify.domain.CertifyType.CERTIFY_PHONE;
import static com.codenear.butterfly.global.exception.ErrorCode.PHONE_NUMBER_ALREADY_USE;

import com.codenear.butterfly.certify.application.CertifyService;
import com.codenear.butterfly.certify.domain.dto.CertifyRequest;
import com.codenear.butterfly.member.domain.Member;
import com.codenear.butterfly.member.domain.dto.MemberDTO;
import com.codenear.butterfly.member.exception.MemberException;
import com.codenear.butterfly.member.infrastructure.MemberDataAccess;
import com.codenear.butterfly.promotion.application.PointPromotionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class PhoneRegistrationService {

    private final CertifyService certifyService;
    private final PointPromotionService pointPromotionService;
    private final MemberDataAccess memberDataAccess;

    public void sendRegistrationCode(String phoneNumber) {
        validatePhoneNumberDuplicate(phoneNumber);
        certifyService.sendCertifyCode(phoneNumber, CERTIFY_PHONE);
    }

    public void checkRegistrationCode(CertifyRequest request, MemberDTO loginMember) {
        certifyService.checkCertifyCode(request, CERTIFY_PHONE);

        Member member = getMemberById(loginMember.getId());
        updatePhoneNumber(request.phoneNumber(), member);

        pointPromotionService.processPromotion(member);
    }

    @Transactional(readOnly = true)
    private Member getMemberById(Long memberId) {
        Member member =  memberDataAccess.findByMemberId(memberId);

        return member;
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
