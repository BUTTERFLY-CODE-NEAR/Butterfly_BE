package com.codenear.butterfly.consent.application;

import com.codenear.butterfly.consent.domain.Consent;
import com.codenear.butterfly.consent.domain.ConsentType;
import com.codenear.butterfly.consent.infrastructure.ConsentDataAccess;
import com.codenear.butterfly.member.domain.Member;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ConsentFacade {

    private final ConsentDataAccess consentDataAccess;

    public List<Consent> getConsents(Long memberId) {
        return consentDataAccess.findConsents(memberId);
    }

    public void saveConsent(ConsentType consentType, boolean agreed, Member member) {
        Consent consent = Consent.create(consentType, agreed, member);
        consentDataAccess.save(consent);
    }
}
