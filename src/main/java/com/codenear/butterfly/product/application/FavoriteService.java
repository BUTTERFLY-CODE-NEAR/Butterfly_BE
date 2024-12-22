package com.codenear.butterfly.product.application;

import com.codenear.butterfly.global.exception.ErrorCode;
import com.codenear.butterfly.member.domain.Member;
import com.codenear.butterfly.member.domain.dto.MemberDTO;
import com.codenear.butterfly.member.domain.repository.member.MemberRepository;
import com.codenear.butterfly.member.exception.MemberException;
import com.codenear.butterfly.product.domain.Product;
import com.codenear.butterfly.product.domain.ProductInventory;
import com.codenear.butterfly.product.domain.dto.ProductViewDTO;
import com.codenear.butterfly.product.domain.repository.ProductInventoryRepository;
import com.codenear.butterfly.product.exception.ProductException;
import com.codenear.butterfly.product.util.ProductMapper;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.codenear.butterfly.product.domain.QFavorite.favorite;

@Service
@Transactional
@RequiredArgsConstructor
public class FavoriteService {

    private final ProductInventoryRepository productInventoryRepository;
    private final MemberRepository memberRepository;
    private final JPAQueryFactory queryFactory;

    @Transactional(readOnly = true)
    public List<ProductViewDTO> getFavoriteAll(Long memberId) {
        List<ProductInventory> favoriteProducts = queryFactory
                .select(favorite.product)
                .from(favorite)
                .where(favorite.member.id.eq(memberId))
                .fetch();

        return favoriteProducts.stream()
                .sorted((p1, p2) -> Boolean.compare(p1.isSoldOut(), p2.isSoldOut()))
                .map(product -> ProductMapper.toProductViewDTO(product, true))
                .toList();
    }

    @Transactional
    public void addFavorite(MemberDTO memberDTO, Long productId) {
        Member member = getMember(memberDTO);
        ProductInventory product = getProduct(productId);

        if (member.hasFavorite(product)) {
            throw new ProductException(ErrorCode.DUPLICATE_FAVORITE, null);
        }

        member.addFavorite(product);
    }

    @Transactional
    public boolean removeFavorite(MemberDTO memberDTO, Long productId) {
        Member member = getMember(memberDTO);
        Product product = getProduct(productId);

        if (member.hasFavorite(product)) {
            member.removeFavorite(product);
            return true;
        }
        return false;
    }

    private Member getMember(MemberDTO memberDTO) {
        return memberRepository.findById(memberDTO.getId())
                .orElseThrow(() -> new MemberException(ErrorCode.SERVER_ERROR, null));
    }

    private ProductInventory getProduct(Long productId) {
        return productInventoryRepository.findById(productId)
                .orElseThrow(() -> new MemberException(ErrorCode.PRODUCT_NOT_FOUND, null));
    }
}
