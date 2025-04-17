package com.codenear.butterfly.promotion.application;

import static com.codenear.butterfly.notify.NotifyMessage.CITATION_PROMOTION;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.codenear.butterfly.notify.fcm.application.FCMFacade;
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
class PointPromotionServiceTest {

    @Mock
    private PromotionDataAccess promotionDataAccess;

    @Mock
    private RecipientRepository recipientRepository;

    @Mock
    private FCMFacade fcmFacade;

    @InjectMocks
    private PointPromotionService promotionService;

    private Member member;
    private Point point;
    private PointPromotion promotion;

    private final String phoneNumber = "01012345678";
    private final Long memberId = 1L;

    @BeforeEach
    void setUp() {
        member = mock(Member.class);
        point = mock(Point.class);
        promotion = mock(PointPromotion.class);

        when(member.getPhoneNumber()).thenReturn(phoneNumber);
        when(member.getId()).thenReturn(memberId);
        when(member.getPoint()).thenReturn(point);
    }

    @Test
    void 휴대폰_인증후_프로모션이_적용된다() {
        int rewardAmount = 1000;
//        when(member.isRecentlyWithdrawn()).thenReturn(false);
        when(member.getNickname()).thenReturn("TestUser");
        when(promotionDataAccess.findPointPromotion(memberId)).thenReturn(promotion);
        when(promotion.isApplicable()).thenReturn(true);
        when(promotion.getRewardAmount()).thenReturn(rewardAmount);
        when(recipientRepository.existsByPhoneNumber(phoneNumber)).thenReturn(false);

        promotionService.processPromotion(member);

//        verify(point).increasePoint(rewardAmount);
        verify(promotion).increaseUsedAmount();
        verify(recipientRepository).save(any(Recipient.class));
        verify(fcmFacade).sendMessage(CITATION_PROMOTION, memberId);
    }

}