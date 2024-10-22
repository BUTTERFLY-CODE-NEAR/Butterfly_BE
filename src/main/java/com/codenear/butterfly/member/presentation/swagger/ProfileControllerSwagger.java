package com.codenear.butterfly.member.presentation.swagger;

import com.codenear.butterfly.global.dto.ResponseDTO;
import com.codenear.butterfly.member.domain.dto.MemberDTO;
import com.codenear.butterfly.member.domain.dto.ProfileUpdateRequestDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

@Tag(name = "Profile", description = "**프로필 API**")
public interface ProfileControllerSwagger {

    @Operation(summary = "유저 프로필 수정", description = "유저 프로필 수정 API")
    @RequestBody(content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
            schema = @Schema(implementation = ProfileUpdateRequestDTO.class)))
    ResponseEntity<ResponseDTO> updateMemberProfile(@Valid @RequestBody ProfileUpdateRequestDTO memberProfileRequestDTO, @AuthenticationPrincipal MemberDTO memberDTO);
}
