package com.codenear.butterfly.promotion.application;

import static com.codenear.butterfly.global.exception.ErrorCode.SERVER_ERROR;

import com.codenear.butterfly.promotion.domain.PointPromotion;
import com.codenear.butterfly.promotion.domain.repository.PointPromotionRepository;
import com.codenear.butterfly.promotion.exception.PromotionException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PromotionDataAccess {

    private final PointPromotionRepository pointPromotionRepository;

//    @Cacheable(value = "pointPromotion", key = "#promotionId")
    @Cacheable(value = "pointPromotion", key = "#promotion?.id", condition = "#result != null")
    public PointPromotion findPointPromotion(Long promotionId) {
        System.out.println("데이터베이스에서 조회: " + promotionId);
        return pointPromotionRepository.findById(promotionId)
                .orElseThrow(() -> new PromotionException(SERVER_ERROR, null));
    }
}
