package com.codenear.butterfly.member.application;

import com.codenear.butterfly.member.domain.Member;
import com.codenear.butterfly.member.domain.dto.MemberDTO;
import com.codenear.butterfly.member.domain.dto.ProfileUpdateRequestDTO;
import com.codenear.butterfly.s3.application.S3Service;
import com.codenear.butterfly.s3.domain.S3Directory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ProfileService {
    private final S3Service s3Service;
    private final MemberService memberService;
    private final NicknameService nicknameService;

    public void updateMemberProfile(ProfileUpdateRequestDTO memberProfileRequestDTO, MemberDTO memberDTO) {

        if (isNotNicknameEquals(memberProfileRequestDTO, memberDTO))
            nicknameService.updateNickname(memberDTO.getId(), memberProfileRequestDTO.getNickname());

        if (memberProfileRequestDTO.getProfileImage() != null) {
            Member member = memberService.loadMemberByMemberId(memberDTO.getId());
            if (member.getProfileImage() != null) {
                s3Service.deleteFile(member.getProfileImage());
            }

            String imageUrl = s3Service.uploadFile(memberProfileRequestDTO.getProfileImage(),
                    S3Directory.PROFILE_IMAGE);
            member.setProfileImage(imageUrl);
        }
    }

    private boolean isNotNicknameEquals(ProfileUpdateRequestDTO memberProfileRequestDTO, MemberDTO memberDTO) {
        return !memberProfileRequestDTO.getNickname().equals(memberDTO.getNickname());
    }
}
