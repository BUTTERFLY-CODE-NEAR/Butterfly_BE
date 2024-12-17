package com.codenear.butterfly.kakaoPay.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(title = "주문 상태 DTO", description = "주문 상태 (READY: 배송 준비 중, DELIVERY: 배송 중, COMPLETED: 배송 완료, CANCELED: 주문 취소)", enumAsRef = true)
public enum OrderStatus {

    READY("배송 준비 중"),
    DELIVERY("배송 중"),
    COMPLETED("배송 완료"),
    CANCELED("주문 취소");

    private final String status;

    OrderStatus(String status) {
        this.status = status;
    }
}
