package com.codenear.butterfly.consent.infrastructure;

import com.codenear.butterfly.consent.domain.Consent;
import com.codenear.butterfly.consent.domain.ConsentType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConsentRepository extends JpaRepository<Consent, Long> {
    List<Consent> findByMemberId(Long memberId);
    List<Consent> findByConsentType(ConsentType consentType);
}
