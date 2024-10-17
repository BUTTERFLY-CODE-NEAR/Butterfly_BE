package com.codenear.butterfly.member.presentation.swagger;

import com.codenear.butterfly.global.dto.ResponseDTO;
import com.codenear.butterfly.member.domain.dto.MemberDTO;
import com.codenear.butterfly.member.domain.dto.ProfileUpdateRequestDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Profile", description = "**프로필 API**")
public interface ProfileControllerSwagger {

    @Operation(summary = "유저 프로필 수정", description = "유저 프로필 수정 API")
    public ResponseEntity<ResponseDTO> updateMemberProfile(@Valid @RequestBody ProfileUpdateRequestDTO memberProfileRequestDTO, @AuthenticationPrincipal MemberDTO memberDTO);
}
