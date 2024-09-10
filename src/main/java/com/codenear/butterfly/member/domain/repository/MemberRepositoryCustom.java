package com.codenear.butterfly.member.domain.repository;

import java.util.Optional;

public interface MemberRepositoryCustom {
    Optional<String> findMaxNumberedNickname(String baseNickname);
}