package com.codenear.butterfly.product.domain.dto;

import java.math.BigDecimal;

public record DiscountRateDTO(int minApplyCount,
                              int maxApplyCount,
                              BigDecimal totalSaleRate) {
}
