package com.codenear.butterfly.promotion.application;

import static com.codenear.butterfly.fcm.domain.FCMMessageConstant.CITATION_PROMOTION;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.codenear.butterfly.fcm.application.FCMFacade;
import com.codenear.butterfly.member.domain.Member;
import com.codenear.butterfly.point.domain.Point;
import com.codenear.butterfly.promotion.domain.PointPromotion;
import com.codenear.butterfly.promotion.domain.Recipient;
import com.codenear.butterfly.promotion.domain.repository.RecipientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CitationPromotionServiceTest {

    @Mock
    private PromotionDataAccess promotionDataAccess;

    @Mock
    private RecipientRepository recipientRepository;

    @Mock
    private FCMFacade fcmFacade;

    @InjectMocks
    private CitationPromotionService promotionService;

    private Member member;
    private Point point;
    private PointPromotion promotion;

    @BeforeEach
    void setUp() {
        String phoneNumber = "01012345678";

        member = mock(Member.class);
        point = mock(Point.class);
        promotion = mock(PointPromotion.class);

        when(member.getPhoneNumber()).thenReturn(phoneNumber);
        when(member.getPoint()).thenReturn(point);
        when(member.getId()).thenReturn(1L);
        when(member.getNickname()).thenReturn("TestUser");

        when(promotionDataAccess.findPointPromotion(1L)).thenReturn(promotion);
        when(promotion.isApplicable()).thenReturn(true);
        when(recipientRepository.existsByPhoneNumber(phoneNumber)).thenReturn(false);
        when(promotion.getRewardAmount()).thenReturn(1000);
    }

    @Test
    void 휴대폰_인증후_프로모션이_적용된다() {
        // when
        promotionService.processPromotion(member);

        // then
        verify(point).increasePoint(1000);
        verify(promotion).increaseUsedAmount();
        verify(recipientRepository).save(any(Recipient.class));
        verify(fcmFacade).sendMessage(CITATION_PROMOTION, 1L);
    }

}