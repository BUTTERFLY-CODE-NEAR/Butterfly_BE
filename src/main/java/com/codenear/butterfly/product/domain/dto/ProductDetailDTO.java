package com.codenear.butterfly.product.domain.dto;

import com.codenear.butterfly.product.domain.Price;
import com.codenear.butterfly.product.domain.ProductInventory;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.List;

@Schema(title = "상품 상세 정보 JSON", description = "상품 상세 정보 요청 시 반환되는 응답 JSON 데이터 입니다.")
public record ProductDetailDTO(
        @Schema(description = "상품 ID") Long productId,
        @Schema(description = "상품 제조사 및 판매처") String companyName,
        @Schema(description = "상품 이름") String productName,
        @Schema(description = "상품 이미지", example = "http://example.com/profile.jpg") String productImage,
        @Schema(description = "상품 원가") Integer originalPrice,
        @Schema(description = "할인률 (%)") BigDecimal saleRate,
        @Schema(description = "상품 할인가") Integer salePrice,
        @Schema(description = "현재 구매 수량") Integer purchaseParticipantCount,
        @Schema(description = "최대 구매 수량") Integer maxPurchaseCount,
        @Schema(description = "좋아요 여부") Boolean isFavorite,
        @Schema(description = "상품 옵션") List<OptionDTO> option,
        @Schema(description = "상품 설명") String description,
        @Schema(description = "개별 내용물의 용량(중량)") String productVolume,
        @Schema(description = "소비기한") String expirationDate,
        @Schema(description = "신청 게이지") Float appliedGauge,
        @Schema(description = "배송 정보") String deliveryInformation,
        @Schema(description = "품절 여부") Boolean isSoldOut,
        @Schema(description = "상품 설명 이미지 리스트") List<ProductDescriptionImageDTO> descriptionImages
) {
    public ProductDetailDTO(ProductInventory product,
                            Price price,
                            boolean isFavorite,
                            BigDecimal saleRate,
                            Float appliedGauge,
                            List<OptionDTO> option,
                            List<ProductDescriptionImageDTO> descriptionImages) {
        this(product.getId(), product.getCompanyName(), product.getProductName(), product.getProductImage(),
                price.originalPrice(), saleRate, price.calculateSalePrice(), product.getPurchaseParticipantCount(),
                product.getMaxPurchaseCount(), isFavorite, option, product.getDescription(), product.getProductVolume(),
                product.getExpirationDate(), appliedGauge, product.getDeliveryInformation(), product.isSoldOut(),
                descriptionImages);

    }
}
