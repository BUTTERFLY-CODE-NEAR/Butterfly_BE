package com.codenear.butterfly.kakaoPay.domain.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class PaymentRequestDTO {
    private long orderId;
    private long memberId;
    private String productName;
    private int quantity;
    private int total;
}
