package com.codenear.butterfly.payment.kakaoPay.domain.dto;

public record ApproveRequestDTO(
        String tid,
        String orderId,
        String memberId,
        String pgToken
) {

}
