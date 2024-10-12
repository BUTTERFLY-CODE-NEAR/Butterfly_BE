package com.codenear.butterfly.kakaoPay.domain.dto;

import lombok.Getter;

@Getter
public class PaymentRequestDTO {
    private String productName;
    private int quantity;
    private int total;
}
