package com.codenear.butterfly.product.util;

import com.codenear.butterfly.product.domain.Option;
import com.codenear.butterfly.product.domain.Price;
import com.codenear.butterfly.product.domain.Product;
import com.codenear.butterfly.product.domain.dto.OptionDTO;
import com.codenear.butterfly.product.domain.dto.ProductDetailDTO;
import com.codenear.butterfly.product.domain.dto.ProductViewDTO;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProductMapper {

    public static ProductViewDTO toProductViewDTO(Product product, boolean isFavorite) {
        Price price = Price.of(product.getOriginalPrice(), product.getSaleRate(), product.getCurrentDiscountRate());
        return new ProductViewDTO(
                product.getId(),
                product.getCompanyName(),
                product.getProductName(),
                product.getProductImage(),
                product.getOriginalPrice(),
                product.getSaleRate(),
                price.calculateSalePrice(),
                product.getPurchaseParticipantCount(),
                product.getMaxPurchaseCount(),
                isFavorite,
                product.isSoldOut()
        );
    }

    public static ProductDetailDTO toProductDetailDTO(Product product, boolean isFavorite) {
        Price price = Price.of(product.getOriginalPrice(), product.getSaleRate(), product.getCurrentDiscountRate());
        List<OptionDTO> optionDTOs = product.getOptions().stream()
                .map(ProductMapper::toOptionDTO)
                .toList();

        return new ProductDetailDTO(
                product.getId(),
                product.getCompanyName(),
                product.getProductName(),
                product.getProductImage(),
                product.getOriginalPrice(),
                product.getSaleRate(),
                price.calculateSalePrice(),
                product.getPurchaseParticipantCount(),
                product.getMaxPurchaseCount(),
                isFavorite,
                optionDTOs,
                product.getDescription(),
                product.getProductVolume(),
                product.getExpirationDate()
        );
    }

    private static OptionDTO toOptionDTO(Option option) {
        BigDecimal originalPriceDecimal = BigDecimal.valueOf(option.getOriginalPrice());
        BigDecimal discount = originalPriceDecimal.multiply(option.getSaleRate())
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        BigDecimal salePrice = originalPriceDecimal.subtract(discount);

        return new OptionDTO(
                option.getId(),
                option.getSubtitle(),
                option.getProductName(),
                option.getProductImage(),
                option.getOriginalPrice(),
                option.getSaleRate(),
                salePrice.intValue()
        );
    }
}