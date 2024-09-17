package com.codenear.butterfly.member.domain.repository.member;

import com.codenear.butterfly.member.domain.Member;
import com.codenear.butterfly.member.domain.Platform;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom {
    Optional<Member> findByEmailAndPlatform(String email, Platform platform);
    Optional<Member> findByEmail(String email);
}