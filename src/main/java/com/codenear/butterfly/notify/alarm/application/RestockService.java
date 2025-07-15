package com.codenear.butterfly.notify.alarm.application;

import com.codenear.butterfly.global.exception.ErrorCode;
import com.codenear.butterfly.member.domain.Member;
import com.codenear.butterfly.member.domain.repository.member.MemberRepository;
import com.codenear.butterfly.member.exception.MemberException;
import com.codenear.butterfly.notify.alarm.domain.Restock;
import com.codenear.butterfly.notify.alarm.domain.dto.RestockResponseDTO;
import com.codenear.butterfly.notify.alarm.infrastructure.RestockRepository;
import com.codenear.butterfly.product.domain.Product;
import com.codenear.butterfly.product.domain.repository.ProductRepository;
import com.codenear.butterfly.product.exception.ProductException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class RestockService {
    private final RestockRepository restockRepository;
    private final ProductRepository productRepository;
    private final MemberRepository memberRepository;

    /**
     * 재입고 알림 저장
     *
     * @param memberId  사용자 아이디
     * @param productId 상품 아이디
     * @return RestockResponseDTO
     */
    public RestockResponseDTO createRestock(final Long memberId, final Long productId) {
        Member member = validateMember(memberId);
        Product product = validateProduct(productId);
        Restock restock = alreadyRestock(member, product);

        if (restock != null) {
            restock.applyRestockNotification();
            return RestockResponseDTO.from(restock);
        }

        restock = Restock.create(member, product);
        restockRepository.save(restock);

        return RestockResponseDTO.from(restock);
    }

    /**
     * 재입고 신청 현황
     *
     * @param memberId  사용자 아이디
     * @param productId 상품 아이디
     * @return boolean
     */
    public boolean existsRestock(final Long memberId, final Long productId) {
        Member member = validateMember(memberId);
        Product product = validateProduct(productId);

        return restockRepository.existsByMemberAndProductAndIsNotifiedFalse(member, product);
    }

    /**
     * 등록되어있는 회원 검증
     *
     * @param memberId 사용자 아이디
     * @return Member
     */
    private Member validateMember(final Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND, "회원을 찾을 수 없습니다."));
    }

    /**
     * 등록되어있는 상품 검증
     *
     * @param productId 상품 아이디
     * @return Product
     */
    private Product validateProduct(final Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ProductException(ErrorCode.PRODUCT_NOT_FOUND, "등록된 상품이 없습니다."));
    }

    /**
     * 이전에 신청한 내역이 있는지 확인
     *
     * @param member  사용자
     * @param product 상품
     * @return Restock
     */
    private Restock alreadyRestock(final Member member, final Product product) {
        return restockRepository.findByMemberAndProduct(member, product);
    }
}
