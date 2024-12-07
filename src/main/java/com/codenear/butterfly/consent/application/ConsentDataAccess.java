package com.codenear.butterfly.consent.application;

import com.codenear.butterfly.consent.domain.Consent;
import com.codenear.butterfly.consent.domain.ConsentRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class ConsentDataAccess {

    private final ConsentRepository consentRepository;

    public List<Consent> findConsents(Long memberId) {
        return consentRepository.findByMemberId(memberId);
    }

    public void save(Consent consent) {
        consentRepository.save(consent);
    }
}
