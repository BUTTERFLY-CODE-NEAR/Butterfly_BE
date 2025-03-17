package com.codenear.butterfly.product.util;

import com.codenear.butterfly.product.domain.Option;
import com.codenear.butterfly.product.domain.Price;
import com.codenear.butterfly.product.domain.ProductImage;
import com.codenear.butterfly.product.domain.ProductInventory;
import com.codenear.butterfly.product.domain.dto.OptionDTO;
import com.codenear.butterfly.product.domain.dto.ProductDetailDTO;
import com.codenear.butterfly.product.domain.dto.ProductImageDTO;
import com.codenear.butterfly.product.domain.dto.ProductViewDTO;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProductMapper {

    public static ProductViewDTO toProductViewDTO(ProductInventory product, boolean isFavorite, Float appliedGauge) {
        Price price = Price.of(
                product.getOriginalPrice(),
                product.getSaleRate(),
                product.getCurrentDiscountRate()
        );

        return new ProductViewDTO(product, price, isFavorite, calculateFinalSaleRate(product), nextSaleRate(product), appliedGauge);
    }

    public static ProductDetailDTO toProductDetailDTO(ProductInventory product, boolean isFavorite, Float appliedGauge) {
        Price price = Price.of(product.getOriginalPrice(), product.getSaleRate(), product.getCurrentDiscountRate());
        List<OptionDTO> optionDTOs = product.getOptions().stream()
                .map(ProductMapper::toOptionDTO)
                .toList();
        List<ProductImageDTO> mainImageDTOs = toProductImageDTOList(product.getProductImage());
        List<ProductImageDTO> descriptionImageDTOs = toProductImageDTOList(product.getDescriptionImages());
        return new ProductDetailDTO(product, price, isFavorite, calculateFinalSaleRate(product), nextSaleRate(product), appliedGauge, optionDTOs, descriptionImageDTOs, mainImageDTOs);
    }

    private static BigDecimal calculateFinalSaleRate(ProductInventory product) {
        return product.getSaleRate().add(product.getCurrentDiscountRate());
    }

    private static OptionDTO toOptionDTO(Option option) {
        BigDecimal originalPriceDecimal = BigDecimal.valueOf(option.getOriginalPrice());
        BigDecimal discount = originalPriceDecimal.multiply(option.getSaleRate())
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        BigDecimal salePrice = originalPriceDecimal.subtract(discount);

        return new OptionDTO(option, salePrice.intValue());
    }

    /**
     * ProductImage (main, description)를 DTO로 매핑
     *
     * @param images 이미지 리스트 (main, description)
     * @return 상품설명 이미지 DTO 리스트
     */
    private static List<ProductImageDTO> toProductImageDTOList(List<ProductImage> images) {
        return images.stream()
                .map(image -> new ProductImageDTO(image.getImageUrl()))
                .toList();
    }

    /**
     * 기존 할인율에 다음 할인율을 더하여 반환한다.
     *
     * @param product
     * @return 할인율
     */
    private static BigDecimal nextSaleRate(ProductInventory product) {
        return product.getSaleRate().add(product.getNextDiscountRate());
    }
}