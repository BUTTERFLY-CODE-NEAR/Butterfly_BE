package com.codenear.butterfly.member.domain.repository.member;

import com.codenear.butterfly.member.domain.Member;
import com.codenear.butterfly.member.domain.Platform;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom {
    Optional<Member> findByNickname(String nickname);
    Optional<Member> findByEmailAndPlatform(String email, Platform platform);
    Optional<Member> findByPhoneNumber(String phoneNumber);
    List<Member> findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByPhoneNumber(String phoneNumber);
}