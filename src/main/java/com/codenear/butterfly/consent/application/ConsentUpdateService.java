package com.codenear.butterfly.consent.application;

import com.codenear.butterfly.consent.domain.Consent;
import com.codenear.butterfly.consent.domain.ConsentType;
import com.codenear.butterfly.consent.dto.ConsentUpdateRequest;
import com.codenear.butterfly.consent.infrastructure.ConsentDataAccess;
import com.codenear.butterfly.notify.fcm.application.FCMFacade;
import com.codenear.butterfly.member.application.MemberService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ConsentUpdateService {

    private final ConsentDataAccess consentDataAccess;
    private final MemberService memberService;
    private final FCMFacade fcmFacade;

    public void updateConsent(ConsentUpdateRequest request, Long memberId) {
        List<Consent> consents = consentDataAccess.findConsents(memberId);

        ConsentType consentType = request.getConsentType();
        Consent consent = getOrCreateConsent(consents, consentType, memberId);

        consent.toggleAgreement();
        if (consent.getConsentType().hasTopic()) {
            updateFCMSubscription(memberId, consent);
        }
        consentDataAccess.save(consent);
    }

    private Consent getOrCreateConsent(List<Consent> consents, ConsentType consentType, Long memberId) {
        return consents.stream()
                .filter(consent -> consent.isSameConsentType(consentType))
                .findFirst()
                .orElseGet(() ->
                        Consent.create(consentType, memberService.loadMemberByMemberId(memberId))
                );
    }

    private void updateFCMSubscription(Long memberId, Consent consent) {
        String topic = consent.getConsentType().getTopic();

        if (consent.isAgreed()) {
            fcmFacade.subscribeToTopic(memberId, topic);
            return;
        }
        fcmFacade.unsubscribeFromTopic(memberId, topic);
    }
}
