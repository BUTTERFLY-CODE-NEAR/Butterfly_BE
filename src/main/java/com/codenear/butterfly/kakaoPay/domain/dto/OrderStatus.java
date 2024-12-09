package com.codenear.butterfly.kakaoPay.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(title = "주문 상태 DTO", description = "주문 상태 (READY: 배송 준비 중, DELIVERY: 배송 중, COMPLETED: 배송 완료, CANCELED: 주문 취소)", enumAsRef = true)
public enum OrderStatus {

    READY,
    DELIVERY,
    COMPLETED,
    CANCELED
}
