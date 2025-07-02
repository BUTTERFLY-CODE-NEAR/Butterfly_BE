package com.codenear.butterfly.product.domain.dto;

import com.codenear.butterfly.product.domain.Price;
import com.codenear.butterfly.product.domain.ProductInventory;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.List;

@Schema(title = "상품 정보 JSON", description = "상품 정보 요청 시 반환되는 응답 JSON 데이터 입니다.")
public record ProductViewDTO(
        @Schema(description = "상품 ID") Long productId,
        @Schema(description = "상품 제조사 및 판매처") String companyName,
        @Schema(description = "상품 이름") String productName,
        @Schema(description = "상품 이미지") List<ProductImageDTO> productImage,
        @Schema(description = "상품 원가") Integer originalPrice,
        @Schema(description = "할인률 (%)") BigDecimal saleRate,
        @Schema(description = "다음 할인율 (%)") BigDecimal nextSaleRate,
        @Schema(description = "상품 할인가") Integer salePrice,
        @Schema(description = "현재 구매 수량") Integer purchaseParticipantCount,
        @Schema(description = "최대 구매 수량") Integer maxPurchaseCount,
        @Schema(description = "좋아요 여부") Boolean isFavorite,
        @Schema(description = "품절 여부") Boolean isSoldOut,
        @Schema(description = "신청 게이지") Float appliedGauge,
        @Schema(description = "배송 정보") String deliveryInformation,
        @Schema(description = "소상공인 여부") boolean isSmallBusinessProduct
) {
    public ProductViewDTO(ProductInventory product,
                          Price price,
                          boolean isFavorite,
                          BigDecimal saleRate,
                          BigDecimal nextSaleRate,
                          Float appliedGauge,
                          List<ProductImageDTO> productImages,
                          boolean isSmallBusinessProduct) {
        this(product.getId(), product.getCompanyName(), product.getProductName(), productImages,
                price.originalPrice(), saleRate, nextSaleRate, price.calculateSalePrice(), product.getPurchaseParticipantCount(),
                product.getMaxPurchaseCount(), isFavorite, product.isSoldOut(), appliedGauge, product.getDeliveryInformation(), isSmallBusinessProduct);
    }
}