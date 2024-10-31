package com.codenear.butterfly.kakaoPay.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(title = "결제 상태 DTO", description = "결제 상태 요청 시 반환되는 데이터입니다.", enumAsRef = true)
public enum PaymentStatus {

    @Schema(description = "결제 정보 없음")
    NONE,
    @Schema(description = "결제 준비 상태")
    READY,
    @Schema(description = "결제 성공 상태")
    SUCCESS,
    @Schema(description = "결제 실패 상태")
    FAIL,
    @Schema(description = "결제 취소 상태")
    CANCEL
}
