package com.codenear.butterfly.product.application;

import com.codenear.butterfly.global.exception.ErrorCode;
import com.codenear.butterfly.member.domain.Member;
import com.codenear.butterfly.member.domain.repository.member.MemberRepository;
import com.codenear.butterfly.member.exception.MemberException;
import com.codenear.butterfly.product.domain.Category;
import com.codenear.butterfly.product.domain.Product;
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

    public List<ProductViewDTO> getAllProducts(Member member) {
        List<Product> products = productRepository.findAll();
        validateProducts(products);
        return convertProductsToDTOs(products, member);
    }

    public List<ProductViewDTO> getProductsByCategory(String categoryValue, Member member) {
        Category category = Category.fromValue(categoryValue);
        List<Product> products = productRepository.findProductByCategory(category);
        validateProducts(products);
        return convertProductsToDTOs(products, member);
    }

    public ProductDetailDTO getProductDetail(Long productId, Member member) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new MemberException(ErrorCode.PRODUCT_NOT_FOUND, null));
        return convertToProductDetailDTO(product, member);
    }

    private void validateProducts(List<Product> products) {
        if (products.isEmpty()) {
            throw new MemberException(ErrorCode.PRODUCT_NOT_FOUND, null);
        }
    }

    private List<ProductViewDTO> convertProductsToDTOs(List<Product> products, Member member) {
        return products.stream()
                .map(product -> convertToProductViewDTO(product, member))
                .toList();
    }

    private Integer calculateSalePrice(Integer originalPrice, BigDecimal saleRate) {
        BigDecimal originalPriceDecimal = BigDecimal.valueOf(originalPrice);
        BigDecimal discount = originalPriceDecimal.multiply(saleRate)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        BigDecimal salePrice = originalPriceDecimal.subtract(discount);
        return salePrice.intValue();
    }

    public boolean isProductFavorite(Member loginMember, Long productId) {
        Member member = memberRepository.findByEmailAndPlatform(loginMember.getEmail(), loginMember.getPlatform())
                .orElseThrow(() -> new MemberException(ErrorCode.SERVER_ERROR, null));
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new MemberException(ErrorCode.PRODUCT_NOT_FOUND, null));
        return favoriteRepository.existsByMemberIdAndProductId(member.getId(), product.getId());
    }

    private ProductViewDTO convertToProductViewDTO(Product product, Member member) {
        boolean isFavorite = isProductFavorite(member, product.getId());
        return new ProductViewDTO(
                product.getId(),
                product.getSubtitle(),
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

    private ProductDetailDTO convertToProductDetailDTO(Product product, Member member) {
        boolean isFavorite = isProductFavorite(member, product.getId());
        return new ProductDetailDTO(
                product.getId(),
                product.getSubtitle(),
                product.getProductName(),
                product.getProductImage(),
                product.getOriginalPrice(),
                product.getSaleRate(),
                calculateSalePrice(product.getOriginalPrice(), product.getSaleRate()),
                product.getPurchaseParticipantCount(),
                product.getMaxPurchaseCount(),
                isFavorite,
                product.getOptions(),
                product.getDescription()
        );
    }
}