package com.codenear.butterfly.payment.domain.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Schema(title = "결제 요청 기본 DTO", description = "결제 요청 시 공통으로 사용되는 데이터입니다.")
@Getter
public abstract class BasePaymentRequestDTO {

    @Schema(description = "상품 이름", example = "참깨라면 * 30 한 박스")
    private String productName;

    @Schema(description = "수량", example = "1")
    private int quantity;

    @Schema(description = "총 금액", example = "30000")
    private int total;

    @Schema(description = "옵션 이름", example = "옵션 B", nullable = true)
    private String optionName;

    @Schema(description = "사용 포인트", example = "500")
    private int point;

    @Schema(description = "주문 아이디 (토스결제 해당)")
    private String orderId;
}