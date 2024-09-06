package com.codenear.butterfly.auth.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

public interface JwtRefreshRepository extends JpaRepository<JwtRefresh, Long> {

    Boolean existsByRefresh(String refresh);

    @Transactional
    void deleteByRefresh(String refresh);
}
