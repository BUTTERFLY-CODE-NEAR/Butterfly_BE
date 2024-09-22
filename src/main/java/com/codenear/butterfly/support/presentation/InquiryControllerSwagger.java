package com.codenear.butterfly.support.presentation;

import com.codenear.butterfly.global.dto.ResponseDTO;
import com.codenear.butterfly.member.domain.Member;
import com.codenear.butterfly.support.domain.dto.InquiryListDTO;
import com.codenear.butterfly.support.domain.dto.InquiryRegisterDTO;
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

@Tag(name = "Inquiry", description = "**고객 문의 API**")
public interface InquiryControllerSwagger {

    @Operation(summary = "고객 문의 리스트", description = "고객 문의 리스트 API")
    @ApiResponses({
            @ApiResponse(responseCode = "body", description = "응답 메시지 예시",
                    content = @Content(schema = @Schema(implementation = InquiryListDTO.class))),
            @ApiResponse(responseCode = "200", description = "Success")
    })
    public ResponseEntity<ResponseDTO> getInquiryList(@AuthenticationPrincipal Member loginMember);

    @Operation(summary = "고객 문의 등록", description = "고객 문의 등록 API")
    public ResponseEntity<ResponseDTO> registerInquiry(@Valid @RequestBody InquiryRegisterDTO dto, @AuthenticationPrincipal Member loginMember);
}
