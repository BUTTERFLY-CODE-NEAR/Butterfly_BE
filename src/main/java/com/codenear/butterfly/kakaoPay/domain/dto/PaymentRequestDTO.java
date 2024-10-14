package com.codenear.butterfly.kakaoPay.domain.dto;

import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
public class PaymentRequestDTO {

    private String orderType;

    // 직거래 시
    private String pickupPlace;
    private LocalDate pickupDate;
    private LocalTime pickupTime;

    // 배달 시
    private Long addressId;
    private LocalDate deliverDate;

    private String productName;
    private int quantity;
    private int total;
}
