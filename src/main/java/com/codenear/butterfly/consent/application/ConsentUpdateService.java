package com.codenear.butterfly.consent.application;

import com.codenear.butterfly.consent.domain.Consent;
import com.codenear.butterfly.consent.domain.ConsentType;
import com.codenear.butterfly.consent.dto.ConsentUpdateRequest;
import com.codenear.butterfly.fcm.application.FCMFacade;
import com.codenear.butterfly.member.application.MemberService;
import com.codenear.butterfly.member.domain.dto.MemberDTO;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ConsentUpdateService {

    private final ConsentDataAccess consentDataAccess;
    private final MemberService memberService;
    private final FCMFacade fcmFacade;

    public void updateConsent(ConsentUpdateRequest request, MemberDTO memberDTO) {
        List<Consent> consents = consentDataAccess.findConsents(memberDTO.getId());

        ConsentType consentType = request.getConsentType();
        Consent findConsent = consents.stream()
                .filter(consent -> consent.getConsentType().equals(consentType))
                .findFirst()
                .orElseGet(() -> Consent.create(consentType, memberService.loadMemberByMemberId(memberDTO.getId())));

        findConsent.toggleAgreement();
        if (findConsent.getConsentType().hasTopic()) {
            updateFCMSubscription(memberDTO, findConsent);
        }
        consentDataAccess.save(findConsent);
    }

    private void updateFCMSubscription(MemberDTO memberDTO, Consent consent) {
        if (consent.isAgreed()) {
            fcmFacade.subscribeToTopic(memberDTO.getId(), consent.getConsentType().getTopic());
            return;
        }
        fcmFacade.unsubscribeFromTopic(memberDTO.getId(), consent.getConsentType().getTopic());
    }
}
