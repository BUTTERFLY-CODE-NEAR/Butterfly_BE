package com.codenear.butterfly.member.application;

import static com.codenear.butterfly.s3.domain.S3Directory.PROFILE_IMAGE;

import com.codenear.butterfly.global.exception.ErrorCode;
import com.codenear.butterfly.member.domain.Member;
import com.codenear.butterfly.member.domain.dto.MemberDTO;
import com.codenear.butterfly.member.domain.dto.MemberInfoDTO;
import com.codenear.butterfly.member.domain.repository.member.MemberRepository;
import com.codenear.butterfly.member.exception.MemberException;
import com.codenear.butterfly.notify.alarm.infrastructure.AlarmRepository;
import com.codenear.butterfly.point.application.PointService;
import com.codenear.butterfly.s3.application.S3Service;
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
    private final S3Service s3Service;
    private final AlarmRepository alarmRepository;

    public MemberInfoDTO getMemberInfo(MemberDTO memberDTO) {
        Integer pointValue = pointService.loadPointByMemberId(memberDTO.getId()).getPoint();
        boolean isNewAlarm = alarmRepository.existsByMemberIdAndIsNewTrue(memberDTO.getId());

        return new MemberInfoDTO(
                memberDTO.getEmail(),
                memberDTO.getPhoneNumber(),
                memberDTO.getNickname(),
                s3Service.generateFileUrl(memberDTO.getProfileImage(), PROFILE_IMAGE),
                0, // TODO : 추후 쿠폰 시스템 도입 후 수정
                memberDTO.getGrade().getGrade(),
                pointValue,
                isNewAlarm
        );
    }

    @Cacheable(value = "userCache", key = "#memberId", condition = "#memberId != null")
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

    @CacheEvict(value = "userCache, memberCache", key = "#memberId")
    public void updateMemberProfileImage(Long memberId, String imageUrl) {
        Member member = loadMemberByMemberId(memberId);
        member.setProfileImage(imageUrl);

    }

    public Member loadMemberByMemberId(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(ErrorCode.SERVER_ERROR, null));
    }
}
