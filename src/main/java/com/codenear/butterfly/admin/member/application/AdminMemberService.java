package com.codenear.butterfly.admin.member.application;

import com.codenear.butterfly.member.domain.Member;
import com.codenear.butterfly.member.domain.repository.member.MemberRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminMemberService {
    private final MemberRepository memberRepository;

    public List<Member> loadAllUsers() {
        return memberRepository.findAll();
    }
}
