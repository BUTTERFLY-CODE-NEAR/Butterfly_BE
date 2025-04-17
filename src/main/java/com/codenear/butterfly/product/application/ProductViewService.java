package com.codenear.butterfly.product.application;

import com.codenear.butterfly.global.exception.ErrorCode;
import com.codenear.butterfly.member.domain.dto.MemberDTO;
import com.codenear.butterfly.member.domain.repository.member.MemberRepository;
import com.codenear.butterfly.member.exception.MemberException;
import com.codenear.butterfly.product.domain.Category;
import com.codenear.butterfly.product.domain.ProductInventory;
import com.codenear.butterfly.product.domain.dto.ProductDetailDTO;
import com.codenear.butterfly.product.domain.dto.ProductViewDTO;
import com.codenear.butterfly.product.domain.repository.FavoriteRepository;
import com.codenear.butterfly.product.domain.repository.ProductInventoryRepository;
import com.codenear.butterfly.product.util.ProductMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProductViewService {

    private final ProductInventoryRepository ProductInventoryRepository;
    private final FavoriteRepository favoriteRepository;
    private final MemberRepository memberRepository;

    public List<ProductViewDTO> getAllProducts(MemberDTO member) {
        List<ProductInventory> products = ProductInventoryRepository.findAll();
        validateProducts(products);
        return products.stream()
                .sorted((p1, p2) -> Boolean.compare(p1.isSoldOut(), p2.isSoldOut()))
                .map(product -> ProductMapper.toProductViewDTO(product, isProductFavorite(member, product.getId()), product.calculateGauge()))
                .toList();
    }

    public List<ProductViewDTO> getProductsByCategory(String categoryValue, MemberDTO member) {
        Category category = Category.fromValue(categoryValue);
        List<ProductInventory> products = ProductInventoryRepository.findProductByCategory(category);
        validateProducts(products);
        return products.stream()
                .sorted((p1, p2) -> Boolean.compare(p1.isSoldOut(), p2.isSoldOut()))
                .map(product -> ProductMapper.toProductViewDTO(product, isProductFavorite(member, product.getId()), product.calculateGauge()))
                .toList();
    }

    public ProductDetailDTO getProductDetail(Long productId, MemberDTO member) {
        ProductInventory product = ProductInventoryRepository.findById(productId)
                .orElseThrow(() -> new MemberException(ErrorCode.PRODUCT_NOT_FOUND, null));
        return ProductMapper.toProductDetailDTO(product, isProductFavorite(member, productId), product.calculateGauge());
    }

    private void validateProducts(List<ProductInventory> products) {
        if (products.isEmpty()) {
            throw new MemberException(ErrorCode.PRODUCT_NOT_FOUND, null);
        }
    }

    public boolean isProductFavorite(MemberDTO member, Long productId) {
        if (member == null) return false;
        return favoriteRepository.existsByMemberIdAndProductId(member.getId(), productId);
    }
}