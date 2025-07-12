package com.codenear.butterfly.payment.domain.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(title = "결제 취소 DTO", description = "결제 취소 시 사용되는 데이터입니다.")
public class CancelRequestDTO {

    @Schema(description = "주문 번호", example = "16자리 숫자")
    String orderCode;
    @Schema(description = "결제 취소 금액", example = "30000")
    String cancelAmount;
    @Schema(description = "취소 사유", example = "단순변심")
    String cancelReason;
    @Schema(description = "결제 아이디")
    String tid;
}
