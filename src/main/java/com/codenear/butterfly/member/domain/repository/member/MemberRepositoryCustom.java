package com.codenear.butterfly.member.domain.repository.member;

import java.util.Optional;

public interface MemberRepositoryCustom {
    Optional<String> findMaxNumberedNickname(String baseNickname);
}