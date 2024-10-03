package com.codenear.butterfly.auth.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface JwtRefreshRepository extends JpaRepository<JwtRefresh, Long> {

    Boolean existsByRefresh(String refresh);
    Optional<JwtRefresh> findByMemberId(Long memberId);

    @Transactional
    void deleteByRefresh(String refresh);
}
