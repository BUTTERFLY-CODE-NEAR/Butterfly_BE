package com.codenear.butterfly.member.domain.repository;

import com.codenear.butterfly.member.domain.Member;
import com.codenear.butterfly.member.domain.Platform;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByEmailAndPlatform(String email, Platform platform);
}
