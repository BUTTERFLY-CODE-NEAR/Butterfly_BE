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
import com.codenear.butterfly.product.util.ProductMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProductViewService {

    private final ProductRepository productRepository;
    private final FavoriteRepository favoriteRepository;
    private final MemberRepository memberRepository;

    public List<ProductViewDTO> getAllProducts(Long memberId) {
        List<Product> products = productRepository.findAll();
        validateProducts(products);
        return products.stream()
                .sorted((p1, p2) -> Boolean.compare(p1.isSoldOut(), p2.isSoldOut()))
                .map(product -> ProductMapper.toProductViewDTO(product, isProductFavorite(memberId, product.getId())))
                .toList();
    }

    public List<ProductViewDTO> getProductsByCategory(String categoryValue, Long memberId) {
        Category category = Category.fromValue(categoryValue);
        List<Product> products = productRepository.findProductByCategory(category);
        validateProducts(products);
        return products.stream()
                .sorted((p1, p2) -> Boolean.compare(p1.isSoldOut(), p2.isSoldOut()))
                .map(product -> ProductMapper.toProductViewDTO(product, isProductFavorite(memberId, product.getId())))
                .toList();
    }

    public ProductDetailDTO getProductDetail(Long productId, Long memberId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new MemberException(ErrorCode.PRODUCT_NOT_FOUND, null));
        return ProductMapper.toProductDetailDTO(product, isProductFavorite(memberId, productId));
    }

    private void validateProducts(List<Product> products) {
        if (products.isEmpty()) {
            throw new MemberException(ErrorCode.PRODUCT_NOT_FOUND, null);
        }
    }

    public boolean isProductFavorite(Long memberId, Long productId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(ErrorCode.SERVER_ERROR, null));
        return favoriteRepository.existsByMemberIdAndProductId(member.getId(), productId);
    }
}