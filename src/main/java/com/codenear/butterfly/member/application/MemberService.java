package com.codenear.butterfly.member.application;

import com.codenear.butterfly.member.domain.dto.MemberDTO;
import com.codenear.butterfly.global.exception.ErrorCode;
import com.codenear.butterfly.member.domain.Member;
import com.codenear.butterfly.member.domain.dto.MemberInfoDTO;
import com.codenear.butterfly.member.domain.repository.member.MemberRepository;
import com.codenear.butterfly.member.exception.MemberException;
import com.codenear.butterfly.point.application.PointService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {
    private final MemberRepository memberRepository;
    private final PointService pointService;

    public MemberInfoDTO getMemberInfo(MemberDTO memberDTO) {
        Integer pointValue = pointService.loadPointByMemberId(memberDTO.getId()).getPoint();

        return new MemberInfoDTO(
                memberDTO.getEmail(),
                memberDTO.getPhoneNumber(),
                memberDTO.getNickname(),
                memberDTO.getProfileImage(),
                0, // TODO : 추후 쿠폰 시스템 도입 후 수정
                memberDTO.getGrade().getGrade(),
                pointValue
        );
    }

    @Cacheable(value = "userCache", key = "#memberId")
    public MemberDTO getMemberDTOByMemberId(Long memberId) {
        Member member = loadMemberByMemberId(memberId);

        return new MemberDTO(
                member.getId(),
                member.getUsername(),
                member.getEmail(),
                member.getPhoneNumber(),
                member.getPassword(),
                member.getNickname(),
                member.getProfileImage(),
                member.getGrade(),
                member.getPlatform()
        );
    }

    @CacheEvict(value = "userCache", key = "#memberId")
    public void updatePhoneNumber(Long memberId, String phoneNumber) {
        Member member = loadMemberByMemberId(memberId);
        member.setPhoneNumber(phoneNumber);
    }

    public Member loadMemberByMemberId(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(ErrorCode.SERVER_ERROR, null));
    }
}
