package com.codenear.butterfly.promotion.application;

import static com.codenear.butterfly.fcm.domain.FCMMessageConstant.CITATION_PROMOTION;

import com.codenear.butterfly.fcm.application.FCMFacade;
import com.codenear.butterfly.member.domain.Member;
import com.codenear.butterfly.point.domain.Point;
import com.codenear.butterfly.promotion.domain.PointPromotion;
import com.codenear.butterfly.promotion.domain.Recipient;
import com.codenear.butterfly.promotion.domain.repository.RecipientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CitationPromotionService {

    private static final long PROMOTION_ID = 1L;

    private final PromotionDataAccess promotionDataAccess;
    private final RecipientRepository recipientRepository;
    private final FCMFacade fcmFacade;

    public void processPromotion(Member member) {
        PointPromotion promotion = promotionDataAccess.findPointPromotion(PROMOTION_ID);

        if (isPromotionApplicable(member.getPhoneNumber(), promotion)) { // 프로모션 가능 여부
            applyPromotion(member.getPoint(), promotion);
            saveRecipient(member);
            sendPromotionMessage(member.getId());
        }
    }

    private boolean isPromotionApplicable(String phoneNumber, PointPromotion promotion) {
        return promotion.isApplicable() && !isPhoneNumberExists(phoneNumber);
    }

    private boolean isPhoneNumberExists(String phoneNumber) {
        return recipientRepository.existsByPhoneNumber(phoneNumber);
    }

    private void applyPromotion(Point point, PointPromotion promotion) {
        point.increasePoint(promotion.getRewardAmount());
        promotion.increaseUsedAmount();
    }

    private void saveRecipient(Member member) {
        Recipient recipient = Recipient.builder()
                .nickname(member.getNickname())
                .phoneNumber(member.getPhoneNumber())
                .build();

        recipientRepository.save(recipient);
    }

    private void sendPromotionMessage(Long memberId) {
        fcmFacade.sendMessage(CITATION_PROMOTION, memberId);
    }
}
