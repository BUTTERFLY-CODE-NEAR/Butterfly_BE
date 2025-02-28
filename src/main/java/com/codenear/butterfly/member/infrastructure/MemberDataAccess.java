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

//    @Cacheable(value = "memberCache", key = "#memberId")
    public Member findByMemberId(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(SERVER_ERROR, null));
    }

    @CacheEvict(value = "userCache, memberCache", key = "#member.id")
    public void save(Member member) {
        memberRepository.save(member);
    }

    public Set<Member> getLinkedAccounts(Member member) {
        Set<Member> linkedAccounts = new LinkedHashSet<>();

        if (member.getEmail() != null) {
            linkedAccounts.addAll(memberRepository.findByEmail(member.getEmail()));
        }

        return linkedAccounts;
    }
}
