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
    public PointPromotion findPointPromotion(Long promotionId) {
        return pointPromotionRepository.findById(promotionId)
                .orElseThrow(() -> new PromotionException(SERVER_ERROR, null));
    }
}
