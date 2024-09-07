package com.codenear.butterfly.auth.domain;

import com.codenear.butterfly.member.domain.Platform;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface JwtRefreshRepository extends JpaRepository<JwtRefresh, Long> {

    Boolean existsByRefresh(String refresh);
    Optional<JwtRefresh> findByEmailAndPlatform(String email, Platform platform);

    @Transactional
    void deleteByRefresh(String refresh);
}
