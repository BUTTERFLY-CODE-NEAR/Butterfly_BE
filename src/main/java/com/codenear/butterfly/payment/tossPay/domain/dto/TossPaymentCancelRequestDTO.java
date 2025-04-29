package com.codenear.butterfly.payment.tossPay.domain.dto;

import com.codenear.butterfly.payment.domain.dto.request.CancelRequestDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class TossPaymentCancelRequestDTO extends CancelRequestDTO {
    @Schema(description = "취소 사유", example = "단순 변심")
    private String cancelReason;

}
