package com.codenear.butterfly.kakaoPay.domain.dto.request;

public record ApproveRequestDTO (
        String tid,
        String orderId,
        String memberId,
        String pgToken
) {

}
