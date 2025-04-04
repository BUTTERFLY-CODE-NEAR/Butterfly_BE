package com.codenear.butterfly.member.infrastructure;

import static com.codenear.butterfly.global.exception.ErrorCode.SERVER_ERROR;

import com.codenear.butterfly.member.domain.Member;
import com.codenear.butterfly.member.domain.repository.member.MemberRepository;
import com.codenear.butterfly.member.exception.MemberException;

import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Transactional
public class MemberDataAccess {

    private final MemberRepository memberRepository;

    public Optional<Member> findByPhoneNumber(String phoneNumber) {
        return memberRepository.findByPhoneNumber(phoneNumber);
    }

    @Cacheable(value = "memberCache", key = "#p0")
    public Member findByMemberId(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(SERVER_ERROR, null));
    }

    @CachePut(cacheNames = {"userCache", "memberCache"}, key = "#result.id")
    public Member save(Member member) {
        return memberRepository.save(member);
    }

    public Set<Member> getLinkedAccounts(Member member) {
        Set<Member> linkedAccounts = new LinkedHashSet<>();

        if (member.getEmail() != null) {
            linkedAccounts.addAll(memberRepository.findByEmail(member.getEmail()));
        }

        return linkedAccounts;
    }

    @CacheEvict(cacheNames = {"userCache", "memberCache"}, key = "#p0")
    public void evictMemberCache(Long memberId) {}
}
