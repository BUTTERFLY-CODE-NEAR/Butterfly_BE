package com.codenear.butterfly.consent.application;

import com.codenear.butterfly.consent.domain.Consent;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ConsentFacade {

    private final ConsentService consentService;

    public List<Consent> getConsentByMemberId(Long id) {
        return consentService.loadConsentsByMemberId(id);
    }
}
