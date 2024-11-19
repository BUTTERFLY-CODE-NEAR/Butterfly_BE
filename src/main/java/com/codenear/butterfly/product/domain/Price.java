package com.codenear.butterfly.product.domain;

import lombok.Value;

import java.math.BigDecimal;
import java.math.RoundingMode;

public record Price(Integer originalPrice, BigDecimal baseDiscountRate, BigDecimal participationDiscountRate) {
    public static Price of(Integer originalPrice,
                           BigDecimal baseDiscountRate,
                           BigDecimal participationDiscountRate) {
        return new Price(originalPrice, baseDiscountRate, participationDiscountRate);
    }

    public Integer calculateSalePrice() {
        BigDecimal originalPriceDecimal = BigDecimal.valueOf(originalPrice);

        // 기본 할인율 적용
        BigDecimal baseDiscount = originalPriceDecimal.multiply(baseDiscountRate)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

        // 참여율 기반 추가 할인
        BigDecimal participationDiscount = originalPriceDecimal.multiply(participationDiscountRate)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

        // 총 할인액
        BigDecimal totalDiscount = baseDiscount.add(participationDiscount);

        return originalPriceDecimal.subtract(totalDiscount).intValue();
    }
}