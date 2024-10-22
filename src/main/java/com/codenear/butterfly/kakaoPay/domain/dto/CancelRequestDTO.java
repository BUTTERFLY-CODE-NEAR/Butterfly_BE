package com.codenear.butterfly.kakaoPay.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(title = "결제 취소 DTO", description = "결제 취소 시 사용되는 데이터입니다.")
public record CancelRequestDTO (
    String tid,
    String cancelAmount
) {

}
