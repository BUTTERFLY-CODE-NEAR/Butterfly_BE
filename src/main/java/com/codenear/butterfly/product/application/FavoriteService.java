package com.codenear.butterfly.product.application;

import com.codenear.butterfly.global.exception.ErrorCode;
import com.codenear.butterfly.member.domain.Member;
import com.codenear.butterfly.member.domain.repository.member.MemberRepository;
import com.codenear.butterfly.member.exception.MemberException;
import com.codenear.butterfly.product.domain.Product;
import com.codenear.butterfly.product.domain.repository.ProductRepository;
import com.codenear.butterfly.product.exception.ProductException;
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

    private final ProductRepository productRepository;
    private final MemberRepository memberRepository;
    private final JPAQueryFactory queryFactory;

    @Transactional(readOnly = true)
    public List<Long> getFavoriteAll(Member loginMember) {
        Long memberId = getMember(loginMember).getId();

        return queryFactory
                .select(favorite.id)
                .from(favorite)
                .where(favorite.member.id.eq(memberId))
                .fetch();
    }

    public void addFavorite(Member loginMember, Long productId) {
        Member member = getMember(loginMember);
        Product product = getProduct(productId);

        if (isFavoriteExists(member, product)) {
            throw new ProductException(ErrorCode.DUPLICATE_FAVORITE, null);
        }

        member.addFavorite(product);
    }

    public void removeFavorite(Member loginMember, Long productId) {
        Member member = getMember(loginMember);
        Product product = getProduct(productId);

        if (!isFavoriteExists(member, product)) {
            throw new ProductException(ErrorCode.FAVORITE_NOT_FOUND, null);
        }

        member.removeFavorite(product);
    }

    private Member getMember(Member loginMember) {
        return memberRepository.findByEmailAndPlatform(loginMember.getEmail(), loginMember.getPlatform())
                .orElseThrow(() -> new MemberException(ErrorCode.SERVER_ERROR, null));
    }

    private Product getProduct(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ProductException(ErrorCode.PRODUCT_NOT_FOUND, null));
    }

    private boolean isFavoriteExists(Member member, Product product) {
        return member.getFavorites().stream()
                .anyMatch(favorite -> favorite.getProduct().equals(product));
    }
}