package com.codenear.butterfly.admin.products.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DiscountRateRequest {
    private double minParticipationRate;
    private double maxParticipationRate;
    private BigDecimal discountRate;
}
