package com.codenear.butterfly.member.application;

import com.codenear.butterfly.member.domain.dto.MemberDTO;
import com.codenear.butterfly.member.domain.dto.ProfileUpdateRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ProfileService {
    private final NicknameService nicknameService;

    public void updateMemberProfile(ProfileUpdateRequestDTO memberProfileRequestDTO, MemberDTO memberDTO) {
        if (isNotNicknameEquals(memberProfileRequestDTO, memberDTO))
            nicknameService.updateNickname(memberDTO.getId(), memberProfileRequestDTO.getNickname());

    }

    private boolean isNotNicknameEquals(ProfileUpdateRequestDTO memberProfileRequestDTO, MemberDTO memberDTO) {
        return !memberProfileRequestDTO.getNickname().equals(memberDTO.getNickname());
    }
}
