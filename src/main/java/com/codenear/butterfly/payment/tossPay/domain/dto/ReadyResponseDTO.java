package com.codenear.butterfly.payment.tossPay.domain.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReadyResponseDTO {
    private String orderId;
}
