package com.codenear.butterfly.kakaoPay.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(title = "결제 취소 DTO", description = "결제 취소 시 사용되는 데이터입니다.")
public class CancelRequestDTO {

    @Schema(description = "주문 id", example = "t로 시작하는 20자리 영어+숫자")
    String tid;
    @Schema(description = "결제 취소 금액", example = "30000")
    String cancelAmount;
}
