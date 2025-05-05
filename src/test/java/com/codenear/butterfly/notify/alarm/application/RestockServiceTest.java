package com.codenear.butterfly.notify.alarm.application;

import com.codenear.butterfly.admin.products.application.AdminProductService;
import com.codenear.butterfly.admin.products.dto.ProductUpdateRequest;
import com.codenear.butterfly.global.exception.ErrorCode;
import com.codenear.butterfly.member.domain.Member;
import com.codenear.butterfly.member.domain.repository.member.MemberRepository;
import com.codenear.butterfly.member.exception.MemberException;
import com.codenear.butterfly.notify.alarm.domain.Restock;
import com.codenear.butterfly.notify.alarm.domain.dto.RestockResponseDTO;
import com.codenear.butterfly.notify.alarm.infrastructure.RestockRepository;
import com.codenear.butterfly.notify.fcm.application.FCMFacade;
import com.codenear.butterfly.payment.domain.repository.PaymentRedisRepository;
import com.codenear.butterfly.product.domain.ProductImage;
import com.codenear.butterfly.product.domain.ProductInventory;
import com.codenear.butterfly.product.domain.repository.KeywordRedisRepository;
import com.codenear.butterfly.product.domain.repository.KeywordRepository;
import com.codenear.butterfly.product.domain.repository.ProductImageRepository;
import com.codenear.butterfly.product.domain.repository.ProductInventoryRepository;
import com.codenear.butterfly.product.domain.repository.ProductRepository;
import com.codenear.butterfly.product.exception.ProductException;
import com.codenear.butterfly.s3.application.S3Service;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.codenear.butterfly.notify.NotifyMessage.RESTOCK_PRODUCT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RestockServiceTest {
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private ProductInventoryRepository productInventoryRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private RestockRepository restockRepository;
    @Mock
    private ProductImageRepository productImageRepository;
    @Mock
    private FCMFacade fcmFacade;
    @Mock
    private S3Service s3Service;
    @Mock
    private KeywordRepository keywordRepository;
    @Mock
    private KeywordRedisRepository keywordRedisRepository;
    @Mock
    private PaymentRedisRepository paymentRedisRepository;

    @InjectMocks
    private RestockService restockService;

    @InjectMocks
    private AdminProductService adminProductService;

    private Long memberId = 1L;
    private Long productId = 1L;
    private Member member;
    private ProductInventory product;

    @BeforeEach
    void setup() {
        member = mock(Member.class);
        product = mock(ProductInventory.class);
    }

    @Test
    void 새로운_재입고_알림을_신청한다() {
        //given
        when(restockRepository.findByMemberAndProduct(member, product)).thenReturn(null);
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        // when
        RestockResponseDTO result = restockService.createRestock(memberId, productId);

        // then
        assertNotNull(result);
        verify(restockRepository).save(any());
    }

    @Test
    void 이전에_신청한상품에_재신청_한다() {
        // given
        Restock existingRestock = mock(Restock.class);
        when(restockRepository.findByMemberAndProduct(member, product)).thenReturn(existingRestock);
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(existingRestock.getMember()).thenReturn(member);
        when(existingRestock.getProduct()).thenReturn(product);

        // when
        restockService.createRestock(memberId, productId);

        // then
        verify(existingRestock).applyRestockNotification();
        verify(restockRepository, never()).save(any());
    }

    @Test
    void 재입고_알림을_발송한다() {
        // given
        Restock restock = mock(Restock.class);
        ProductUpdateRequest request = mock(ProductUpdateRequest.class);

        MultipartFile dummyFile = mock(MultipartFile.class);
        when(dummyFile.isEmpty()).thenReturn(false);

        when(restock.getMember()).thenReturn(member);
        when(request.getProductImage()).thenReturn(List.of(dummyFile));
        when(request.getDescriptionImages()).thenReturn(List.of(dummyFile));

        when(request.getStockQuantity()).thenReturn(10);
        when(product.getStockQuantity()).thenReturn(0);
        when(product.getRestocks()).thenReturn(Set.of(restock));
        when(product.getId()).thenReturn(productId);

        when(productImageRepository.findAllByProductIdAndImageType(productId, ProductImage.ImageType.MAIN)).thenReturn(List.of());
        when(productImageRepository.findAllByProductIdAndImageType(productId, ProductImage.ImageType.DESCRIPTION)).thenReturn(List.of());
        when(productInventoryRepository.findById(productId)).thenReturn(Optional.of(product));

        // when
        adminProductService.updateProduct(productId, request);

        // then
        verify(fcmFacade).sendMessage(RESTOCK_PRODUCT, member.getId());
        verify(restock).sendNotification();
    }

    @Test
    void 재입고_신청_현황을_반환한다() {
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(restockRepository.existsByMemberAndProductAndIsNotifiedFalse(member, product)).thenReturn(true);

        boolean result = restockService.existsRestock(memberId, productId);

        assertThat(result).isTrue();
    }

    @Test
    void 존재하지_않는_회원이면_예외발생() {
        // when
        when(memberRepository.findById(memberId)).thenReturn(Optional.empty());

        // then
        assertThatThrownBy(() -> restockService.createRestock(memberId, productId))
                .isInstanceOf(MemberException.class)
                .hasMessageContaining(ErrorCode.MEMBER_NOT_FOUND.getMessage());
    }

    @Test
    void 존재하지_않는_상품이면_예외발생() {
        // when
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        // then
        assertThatThrownBy(() -> restockService.createRestock(memberId, productId))
                .isInstanceOf(ProductException.class)
                .hasMessageContaining(ErrorCode.PRODUCT_NOT_FOUND.getMessage());
    }
}
