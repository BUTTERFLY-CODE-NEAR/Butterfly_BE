package com.codenear.butterfly.admin.products.dto;

import java.math.BigDecimal;
import java.util.List;

public record ProductCreateRequest(
        String productName,
        String companyName,
        String description,
        Integer originalPrice,
        BigDecimal saleRate,
        String category,
        Integer quantity,
        Integer purchaseParticipantCount,
        Integer maxPurchaseCount,
        Integer stockQuantity,
        List<String> keywords
) {

}