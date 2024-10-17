package com.codenear.butterfly.member.presentation;

import com.codenear.butterfly.global.dto.ResponseDTO;
import com.codenear.butterfly.global.util.ResponseUtil;
import com.codenear.butterfly.member.application.ProfileService;
import com.codenear.butterfly.member.domain.dto.MemberDTO;
import com.codenear.butterfly.member.domain.dto.ProfileUpdateRequestDTO;
import com.codenear.butterfly.member.presentation.swagger.ProfileControllerSwagger;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/member/profile")
@RequiredArgsConstructor
public class ProfileController implements ProfileControllerSwagger {
    private final ProfileService profileService;

    @PatchMapping
    public ResponseEntity<ResponseDTO> updateMemberProfile(@Valid @RequestBody ProfileUpdateRequestDTO memberProfileRequestDTO,
                                                           @AuthenticationPrincipal MemberDTO memberDTO) {
        profileService.updateMemberProfile(memberProfileRequestDTO, memberDTO);
        return ResponseUtil.createSuccessResponse(null);
    }
}
