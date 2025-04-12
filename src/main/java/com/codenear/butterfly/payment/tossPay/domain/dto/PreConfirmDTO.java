package com.codenear.butterfly.payment.tossPay.domain.dto;

import lombok.Getter;

@Getter
public class PreConfirmDTO {
    private int quantity;
    private int totalAmount;
    private int point;
}
