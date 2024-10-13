package com.codenear.butterfly.consent.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConsentRepository extends JpaRepository<Consent, Long> {
    List<Consent> findByMemberId(Long memberId);
}
