package com.codenear.butterfly.member.application;

import com.codenear.butterfly.member.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MemberFacade {

    private final MemberService memberService;

    public Member getMember(Long id) {
        return memberService.loadMemberByMemberId(id);
    }
}
