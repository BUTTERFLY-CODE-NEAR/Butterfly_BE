package com.codenear.butterfly.member.application;

import static com.codenear.butterfly.member.util.ImageResizeUtil.resizeImage;
import static com.codenear.butterfly.s3.domain.S3Directory.PROFILE_IMAGE;

import com.codenear.butterfly.member.domain.dto.MemberDTO;
import com.codenear.butterfly.member.domain.dto.ProfileUpdateRequestDTO;
import com.codenear.butterfly.s3.application.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional
public class ProfileService {
    private final S3Service s3Service;
    private final MemberService memberService;
    private final NicknameService nicknameService;

    public void updateMemberProfile(ProfileUpdateRequestDTO requestDTO, MemberDTO memberDTO) {
        updateNicknameIfChanged(requestDTO, memberDTO);
        updateProfileImageIfPresent(requestDTO, memberDTO);
    }

    private void updateNicknameIfChanged(ProfileUpdateRequestDTO requestDTO, MemberDTO memberDTO) {
        if (!requestDTO.getNickname().equals(memberDTO.getNickname())) {
            nicknameService.updateNickname(memberDTO.getId(), requestDTO.getNickname());
        }
    }

    private void updateProfileImageIfPresent(ProfileUpdateRequestDTO requestDTO, MemberDTO memberDTO) {
        if (requestDTO.getProfileImage() != null) {
            deleteExistingProfileImage(memberDTO);
            String imageUrl = uploadNewProfileImage(requestDTO.getProfileImage());
            memberService.updateMemberProfileImage(memberDTO.getId(), imageUrl);
        }
    }

    private void deleteExistingProfileImage(MemberDTO memberDTO) {
        if (memberDTO.getProfileImage() != null) {
            s3Service.deleteFile(memberDTO.getProfileImage(), PROFILE_IMAGE);
        }
    }

    private String uploadNewProfileImage(MultipartFile profileImage) {
        MultipartFile resized = resizeImage(profileImage); // 이미지 리사이즈
        return s3Service.uploadFile(resized, PROFILE_IMAGE);
    }
}
