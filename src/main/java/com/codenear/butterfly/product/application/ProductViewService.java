package com.codenear.butterfly.product.application;

import com.codenear.butterfly.global.exception.ErrorCode;
import com.codenear.butterfly.member.exception.MemberException;
import com.codenear.butterfly.product.domain.Category;
import com.codenear.butterfly.product.domain.Product;
import com.codenear.butterfly.product.domain.dto.ProductViewDTO;
import com.codenear.butterfly.product.domain.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductViewService {

    private final ProductRepository productRepository;

    public List<ProductViewDTO> getProductsByCategory(String categoryValue) {
        Category category = Category.fromValue(categoryValue);
        List<Product> products = productRepository.findProductByCategory(category);

        if (products.isEmpty()) {
            throw new MemberException(ErrorCode.PRODUCT_NOT_FOUND, null);
        }

        return products.stream()
                .map(this::convertToProductViewDTO)
                .toList();
    }

    //todo: 상품 상세 엔티티 생성 후 코드 수정
    public ProductViewDTO getProductDetail(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new MemberException(ErrorCode.SERVER_ERROR, null));

        return convertToProductViewDTO(product);
    }

    private Integer calculateSalePrice(Integer originalPrice, BigDecimal saleRate) {
        BigDecimal originalPriceDecimal = new BigDecimal(originalPrice);
        BigDecimal discount = originalPriceDecimal.multiply(saleRate).divide(BigDecimal.valueOf(100));
        BigDecimal salePrice = originalPriceDecimal.subtract(discount);

        return salePrice.setScale(0, RoundingMode.HALF_UP).intValue();
    }

    private ProductViewDTO convertToProductViewDTO(Product product) {
        return new ProductViewDTO(
                product.getProductName(),
                product.getProductImage(),
                product.getOriginalPrice(),
                product.getSaleRate(),
                calculateSalePrice(product.getOriginalPrice(), product.getSaleRate()),
                product.getPurchaseParticipantCount(),
                product.getMaxPurchaseCount()
        );
    }
}