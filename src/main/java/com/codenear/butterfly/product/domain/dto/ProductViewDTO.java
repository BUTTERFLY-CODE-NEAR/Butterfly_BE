package com.codenear.butterfly.product.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(title = "상품 정보 JSON", description = "상품 정보 요청 시 반환되는 응답 JSON 데이터 입니다.")
public record ProductViewDTO(
        @Schema(description = "상품 ID") Long productId,
        @Schema(description = "상품 제조사 및 판매처") String subtitle,
        @Schema(description = "상품 이름") String productName,
        @Schema(description = "상품 이미지", example = "http://example.com/profile.jpg") String productImage,
        @Schema(description = "상품 원가") Integer originalPrice,
        @Schema(description = "할인률 (%)") BigDecimal saleRate,
        @Schema(description = "상품 할인가") Integer salePrice,
        @Schema(description = "현재 구매 수량") Integer purchaseParticipantCount,
        @Schema(description = "최대 구매 수량") Integer maxPurchaseCount,
        @Schema(description = "좋아요 여부") Boolean isFavorite
) {
}
