package com.codenear.butterfly.payment.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(title = "결제 상태 DTO", description = "결제 상태 (NONE: 결제정보없음, READY: 결제준비, SUCCESS: 결제성공, FAIL: 결제실패, CANCEL: 결제취소)", enumAsRef = true)
public enum PaymentStatus {

    NONE,
    READY,
    SUCCESS,
    FAIL,
    CANCEL
}
