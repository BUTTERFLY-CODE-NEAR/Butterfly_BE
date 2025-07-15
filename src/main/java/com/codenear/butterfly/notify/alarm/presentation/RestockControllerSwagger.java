package com.codenear.butterfly.notify.alarm.presentation;

import com.codenear.butterfly.global.dto.ResponseDTO;
import com.codenear.butterfly.member.domain.dto.MemberDTO;
import com.codenear.butterfly.notify.alarm.domain.dto.RestockResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;

@Tag(name = "Alarm", description = "**재입고 알림 API**")
public interface RestockControllerSwagger {
    @Operation(summary = "재입고 신청", description = "Sold Out 된 상품의 재입고 알림 신청 API")
    @ApiResponses({
            @ApiResponse(responseCode = "body", description = "응답 메시지 예시",
                    content = @Content(schema = @Schema(implementation = RestockResponseDTO.class))),
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "40400", description = "등록된 상품 정보가 없을 때"),
            @ApiResponse(responseCode = "40402", description = "등록된 회원 정보가 없을 때")

    })
    ResponseEntity<ResponseDTO> createRestock(@AuthenticationPrincipal MemberDTO member,
                                              @PathVariable(name = "product_id") Long productId);

    @Operation(summary = "재입고 신청 현황", description = "해당 상품에 대한 재입고 신청 여부 확인")
    @ApiResponses({
            @ApiResponse(responseCode = "body", description = "응답 메시지 예시",
                    content = @Content(schema = @Schema(implementation = Boolean.class))),
            @ApiResponse(responseCode = "200", description = "Success")
    })
    ResponseEntity<ResponseDTO> existsRestock(@AuthenticationPrincipal MemberDTO member,
                                              @PathVariable(name = "product_id") Long productId);
}
