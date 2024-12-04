package com.codenear.butterfly.member.infrastructure;

import com.codenear.butterfly.member.domain.Member;
import com.codenear.butterfly.member.domain.repository.member.MemberRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MemberDataAccess {

    private final MemberRepository memberRepository;

    public Optional<Member> findByPhoneNumber(String phoneNumber) {
        return memberRepository.findByPhoneNumber(phoneNumber);
    }
}
