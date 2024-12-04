package com.codenear.butterfly.consent.presentation;

import com.codenear.butterfly.consent.dto.ConsentInfoResponseDTO;
import com.codenear.butterfly.consent.dto.ConsentUpdateRequest;
import com.codenear.butterfly.global.dto.ResponseDTO;
import com.codenear.butterfly.member.domain.dto.MemberDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Consent", description = "**수신 동의 API**")
public interface ConsentControllerSwagger {

    @Operation(summary = "수신 동의 리스트", description = "수신 동의 리스트 API")
    @ApiResponses({
            @ApiResponse(responseCode = "body", description = "응답 메시지 예시",
                    content = @Content(schema = @Schema(implementation = ConsentInfoResponseDTO.class))),
            @ApiResponse(responseCode = "200", description = "Success")
    })
    ResponseEntity<ResponseDTO> getConsents(@AuthenticationPrincipal MemberDTO memberDTO);

    @Operation(summary = "수신 동의 변경", description = "수신 동의 변경 API")
    ResponseEntity<ResponseDTO> updateConsent(@Valid @RequestBody ConsentUpdateRequest updateRequestDTO, @AuthenticationPrincipal MemberDTO memberDTO);
}
