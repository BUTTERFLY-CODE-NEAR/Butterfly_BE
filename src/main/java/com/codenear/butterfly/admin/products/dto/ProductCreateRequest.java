package com.codenear.butterfly.admin.products.dto;

import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

public record ProductCreateRequest(
        MultipartFile productImage,
        String productName,
        String companyName,
        String description,
        String productVolume,
        String expirationDate,
        Integer originalPrice,
        BigDecimal saleRate,
        String category,
        Integer quantity,
        Integer purchaseParticipantCount,
        Integer maxPurchaseCount,
        Integer stockQuantity,
        List<String> keywords,
        String deliveryInformation
) {

}