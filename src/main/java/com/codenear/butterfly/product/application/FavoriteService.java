package com.codenear.butterfly.product.application;

import com.codenear.butterfly.global.exception.ErrorCode;
import com.codenear.butterfly.member.domain.Member;
import com.codenear.butterfly.member.domain.dto.MemberDTO;
import com.codenear.butterfly.member.domain.repository.member.MemberRepository;
import com.codenear.butterfly.member.exception.MemberException;
import com.codenear.butterfly.product.domain.Product;
import com.codenear.butterfly.product.domain.dto.ProductViewDTO;
import com.codenear.butterfly.product.domain.repository.ProductRepository;
import com.codenear.butterfly.product.exception.ProductException;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

import static com.codenear.butterfly.product.domain.QFavorite.favorite;

@Service
@Transactional
@RequiredArgsConstructor
public class FavoriteService {

    private final ProductRepository productRepository;
    private final MemberRepository memberRepository;
    private final JPAQueryFactory queryFactory;

    @Transactional(readOnly = true)
    public List<ProductViewDTO> getFavoriteAll(Long memberId) {
        List<Product> favoriteProducts = queryFactory
                .select(favorite.product)
                .from(favorite)
                .where(favorite.member.id.eq(memberId))
                .fetch();

        return convertProductsToDTOs(favoriteProducts);
    }

    private List<ProductViewDTO> convertProductsToDTOs(List<Product> products) {
        return products.stream()
                .map(this::convertToProductViewDTO)
                .toList();
    }

    private ProductViewDTO convertToProductViewDTO(Product product) {
        boolean isFavorite = true;
        int salePrice = calculateSalePrice(product.getOriginalPrice(), product.getSaleRate());

        return new ProductViewDTO(
                product.getId(),
                product.getCompanyName(),
                product.getProductName(),
                product.getProductImage(),
                product.getOriginalPrice(),
                product.getSaleRate(),
                salePrice,
                product.getPurchaseParticipantCount(),
                product.getMaxPurchaseCount(),
                isFavorite
        );
    }

    private Integer calculateSalePrice(Integer originalPrice, BigDecimal saleRate) {
        BigDecimal originalPriceDecimal = BigDecimal.valueOf(originalPrice);
        BigDecimal discount = originalPriceDecimal.multiply(saleRate)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        BigDecimal salePrice = originalPriceDecimal.subtract(discount);
        return salePrice.intValue();
    }

    public void addFavorite(MemberDTO memberDTO, Long productId) {
        Member member = getMember(memberDTO);
        Optional<Product> product = getProduct(productId);

        if (isFavoriteExists(member, product.orElse(null))) {
            throw new ProductException(ErrorCode.DUPLICATE_FAVORITE, null);
        }

        member.addFavorite(product.orElse(null));
    }

    public boolean removeFavorite(MemberDTO memberDTO, Long productId) {
        Member member = getMember(memberDTO);
        Optional<Product> product = getProduct(productId);

        if (product.isPresent() && isFavoriteExists(member, product.get())) {
            member.removeFavorite(product.get());
            return true;
        }
        return false;
    }

    private Member getMember(MemberDTO memberDTO) {
        return memberRepository.findById(memberDTO.getId())
                .orElseThrow(() -> new MemberException(ErrorCode.SERVER_ERROR, null));
    }

    private Optional<Product> getProduct(Long productId) {
        return productRepository.findById(productId);
    }

    private boolean isFavoriteExists(Member member, Product product) {
        return member.getFavorites().stream()
                .anyMatch(favorite -> favorite.getProduct().equals(product));
    }
}
