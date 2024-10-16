package com.codenear.butterfly.kakaoPay.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.time.LocalDate;

@Schema(title = "배달 결제 요청 JSON", description = "배달 결제 요청 시 사용되는 JSON 데이터입니다.")
@Getter
public class DeliveryPaymentRequestDTO extends BasePaymentRequestDTO {

    @Schema(description = "배달 주소 ID", example = "1")
    private Long addressId;

    @Schema(description = "배달 날짜", example = "2024-10-21")
    private LocalDate deliverDate;
}