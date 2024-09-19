package com.codenear.butterfly.product.domain.dto;

import java.math.BigDecimal;

public record ProductViewDTO(String productName, String productImage, Integer originalPrice, BigDecimal saleRate, Integer salePrice, Integer purchaseParticipantCount, Integer MaxPurchaseCount) {
}
