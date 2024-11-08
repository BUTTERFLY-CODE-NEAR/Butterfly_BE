package com.codenear.butterfly.product.application;

import com.codenear.butterfly.global.exception.ErrorCode;
import com.codenear.butterfly.member.domain.Member;
import com.codenear.butterfly.member.domain.repository.member.MemberRepository;
import com.codenear.butterfly.member.exception.MemberException;
import com.codenear.butterfly.product.domain.Category;
import com.codenear.butterfly.product.domain.Product;
import com.codenear.butterfly.product.domain.dto.OptionDTO;
import com.codenear.butterfly.product.domain.dto.ProductDetailDTO;
import com.codenear.butterfly.product.domain.dto.ProductViewDTO;
import com.codenear.butterfly.product.domain.repository.FavoriteRepository;
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
    private final FavoriteRepository favoriteRepository;
    private final MemberRepository memberRepository;

    public List<ProductViewDTO> getAllProducts(Long memberId) {
        List<Product> products = productRepository.findAll();
        validateProducts(products);
        return convertProductsToDTOs(products, memberId);
    }

    public List<ProductViewDTO> getProductsByCategory(String categoryValue, Long memberId) {
        Category category = Category.fromValue(categoryValue);
        List<Product> products = productRepository.findProductByCategory(category);
        validateProducts(products);
        return convertProductsToDTOs(products, memberId);
    }

    public ProductDetailDTO getProductDetail(Long productId, Long memberId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new MemberException(ErrorCode.PRODUCT_NOT_FOUND, null));
        return convertToProductDetailDTO(product, memberId);
    }

    private void validateProducts(List<Product> products) {
        if (products.isEmpty()) {
            throw new MemberException(ErrorCode.PRODUCT_NOT_FOUND, null);
        }
    }

    private List<ProductViewDTO> convertProductsToDTOs(List<Product> products, Long memberId) {
        return products.stream()
                .map(product -> convertToProductViewDTO(product, memberId))
                .toList();
    }

    private Integer calculateSalePrice(Integer originalPrice, BigDecimal saleRate) {
        BigDecimal originalPriceDecimal = BigDecimal.valueOf(originalPrice);
        BigDecimal discount = originalPriceDecimal.multiply(saleRate)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        BigDecimal salePrice = originalPriceDecimal.subtract(discount);
        return salePrice.intValue();
    }

    public boolean isProductFavorite(Long memberId, Long productId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(ErrorCode.SERVER_ERROR, null));
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new MemberException(ErrorCode.PRODUCT_NOT_FOUND, null));
        return favoriteRepository.existsByMemberIdAndProductId(member.getId(), product.getId());
    }

    private ProductViewDTO convertToProductViewDTO(Product product, Long memberId) {
        boolean isFavorite = isProductFavorite(memberId, product.getId());
        return new ProductViewDTO(
                product.getId(),
                product.getCompanyName(),
                product.getProductName(),
                product.getProductImage(),
                product.getOriginalPrice(),
                product.getSaleRate(),
                calculateSalePrice(product.getOriginalPrice(), product.getSaleRate()),
                product.getPurchaseParticipantCount(),
                product.getMaxPurchaseCount(),
                isFavorite
        );
    }

    private ProductDetailDTO convertToProductDetailDTO(Product product, Long memberId) {
        boolean isFavorite = isProductFavorite(memberId, product.getId());

        List<OptionDTO> optionDTO = product.getOptions().stream()
                .map(option -> new OptionDTO(
                        option.getId(),
                        option.getSubtitle(),
                        option.getProductName(),
                        option.getProductImage(),
                        option.getOriginalPrice(),
                        option.getSaleRate(),
                        calculateSalePrice(option.getOriginalPrice(), option.getSaleRate())
                )).toList();

        return new ProductDetailDTO(
                product.getId(),
                product.getCompanyName(),
                product.getProductName(),
                product.getProductImage(),
                product.getOriginalPrice(),
                product.getSaleRate(),
                calculateSalePrice(product.getOriginalPrice(), product.getSaleRate()),
                product.getPurchaseParticipantCount(),
                product.getMaxPurchaseCount(),
                isFavorite,
                optionDTO,
                product.getDescription()
        );
    }
}
