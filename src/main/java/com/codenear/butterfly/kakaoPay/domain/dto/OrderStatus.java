package com.codenear.butterfly.kakaoPay.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(title = "주문 상태 DTO", description = "결제 상태 (READY: 배송 준비 중, DELIVERY: 배송 중, COMPLETED: 배송 완료)", enumAsRef = true)
public enum OrderStatus {

    READY,
    DELIVERY,
    COMPLETED
}
